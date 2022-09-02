package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinanceProjection {

  public static final long DEFAULT_TAKE_NTH = 1L;

  /**
   * encapsulate the counter and 
   * faster predicate than using modulo.
   */
  public static class TakeNth<T> implements Predicate<T> {
    private long index;
    private long nth;

    public TakeNth(long nth) {
      this.nth = nth;
      this.index = 1;
    }
    @Override
    public boolean test(T ignore) {
      index = index == nth ? 1 : index + 1;
      return index == 1;
    }
  }

  public static class PaymentAccumulator {
    
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("principal")
    private BigDecimal principal;

    @JsonProperty("interest")
    private BigDecimal interest;

    public PaymentAccumulator addPayment(LoanAmortization.Payment payment) {
      amount = amount.add(BigDecimal.valueOf(payment.getAmount()).setScale(2, RoundingMode.HALF_EVEN));
      principal = principal.add(BigDecimal.valueOf(payment.getPrincipal()).setScale(2, RoundingMode.HALF_EVEN));
      interest = interest.add(BigDecimal.valueOf(payment.getInterest()).setScale(2, RoundingMode.HALF_EVEN));
      return this;
    }

    public BigDecimal getAmount() {
      return amount;
    }
    public BigDecimal getPrincipal() {
      return principal;
    }
    public BigDecimal getInterest() {
      return interest;
    }

    private PaymentAccumulator(Builder builder) {
      amount = builder.amount;
      principal = builder.principal;
      interest = builder.interest;
    }

    public static class Builder {
      private BigDecimal amount;
      private BigDecimal principal;
      private BigDecimal interest;

      public Builder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
      }
      public Builder principal(BigDecimal principal) {
        this.principal = principal;
        return this;
      }
      public Builder interest(BigDecimal interest) {
        this.interest = interest;
        return this;
      }
      public PaymentAccumulator build() {
        return new PaymentAccumulator(this);
      }
    }
  }

  public static class Loan {

    // name of this loan
    @JsonProperty("label")
    private String label;
      
    // the starting period for finance segment
    @JsonProperty("start")
    private long start;

    // the ending period for finance segment
    @JsonProperty("end")
    private long end;

    // upfront const on securing the loans. fees, points, etc.
    @JsonProperty("cost")
    private double cost;

    // amortized loan
    @JsonProperty("loanDetails")
    private LoanAmortization.Loan loanDetails;
  
    public Loan() {}

    public Loan setLabel(String label) {
      this.label = label;
      return this;
    }
    public String getLabel() {
      return label;
    }
    public Loan setStart(long start) {
      this.start = start;
      return this;
    }
    public long getStart() {
      return start;
    }
    public Loan setEnd(long end) {
      this.end = end;
      return this;
    }
    public long getEnd() {
      return end;
    }
    public Loan setCost(double cost) {
      this.cost = cost;
      return this;
    }
    public double getCost() {
      return cost;
    }
    public Loan setLoanDetails(LoanAmortization.Loan loanDetails) {
      this.loanDetails = loanDetails;
      return this;
    }
    public LoanAmortization.Loan getLoanDetails() {
      return loanDetails;
    }

    private Loan(Builder builder) {
      label = builder.label;
      start = builder.start;
      end = builder.end;
      cost = builder.cost;
      loanDetails = builder.loanDetails;
    }

    public static class Builder {
      
      private String label;
      private long start;
      private long end;
      private double cost;
      private LoanAmortization.Loan loanDetails;

      public Builder label(String label) {
        this.label = label;
        return this;
      }
      public Builder start(long start) {
        this.start = start;
        return this;
      }
      public Builder end(long end) {
        this.end = end;
        return this;
      }
      public Builder cost(double cost) {
        this.cost = cost;
        return this;
      }
      public Builder loanDetails(LoanAmortization.Loan loanDetails) {
        this.loanDetails = loanDetails;
        return this;
      }
      public Loan build() {
        return new Loan(this);
      }
    }
  }

  private final List<Loan> loans;

  private final Map<String, Loan> loansByLabel;

  private final Map<String, LoanAmortization> loanAmortizations;

  private List<LoanAmortization.Payment> projectedPayments;

  private List<PaymentAccumulator> projectedPaymentAccumulation;

  private Map<String, List<PaymentAccumulator>> projectedPaymentAccumulationOnIndividualLoans;

  public FinanceProjection(List<Loan> loans) {
    this.loans = loans;
    this.loansByLabel =
      loans.stream()
        .collect(Collectors.toMap(
          loan -> loan.getLabel(),
          loan -> loan
        ));
    this.loanAmortizations = 
      loans.stream()
        .collect(Collectors.toMap(
          loan -> loan.getLabel(),
          loan -> new LoanAmortization(loan.getLoanDetails())));
  }

  public List<Loan> getLoans() {
    return loans;
  }

  public Loan getLoan(String label) {
    return loansByLabel.get(label);
  }

  public Map<String, LoanAmortization> getLoanAmortizations() {
    return loanAmortizations;
  }

  public List<LoanAmortization.Payment> getProjectedPayments() {

    if (projectedPayments == null) {
      this.projectedPayments = 
        loans.stream()
             .flatMap(loan -> loanAmortizations.get(loan.getLabel()).getAmortization().stream().limit(loan.getEnd() - loan.getStart()))
             .collect(Collectors.toList());
    }

    return this.projectedPayments;
  }

  public List<PaymentAccumulator> getProjectedPaymentAccumulationOnIndividualLoan(String loanLabel) {
    return getProjectedPaymentAccumulationOnIndividualLoan(loanLabel, DEFAULT_TAKE_NTH);
  }

  public List<PaymentAccumulator> getProjectedPaymentAccumulationOnIndividualLoan(String loanLabel, long takeNth) {
    if (projectedPaymentAccumulationOnIndividualLoans == null) {
      projectedPaymentAccumulationOnIndividualLoans = new HashMap<>();
    }

    return projectedPaymentAccumulationOnIndividualLoans.computeIfAbsent(loanLabel, (label) -> {
      final PaymentAccumulator accumulator =
        new PaymentAccumulator.Builder()
          .amount(BigDecimal.ZERO)
          .principal(BigDecimal.ZERO)
          .interest(BigDecimal.ZERO)
          .build();
      final Loan loan = loansByLabel.get(label);
      final List<LoanAmortization.Payment> projectedPayments = loanAmortizations.get(loan.getLabel()).getAmortization();
      final TakeNth<PaymentAccumulator> filterOnNth = new TakeNth<>(takeNth);
      return projectedPayments.stream()
        .map(payment -> {
          accumulator.addPayment(payment);
          return new PaymentAccumulator.Builder()
                                .amount(accumulator.getAmount())
                                .principal(accumulator.getPrincipal())
                                .interest(accumulator.getInterest())
                                .build();
        })
        .filter(filterOnNth)
        .collect(Collectors.toList());
    });
  }

  public List<PaymentAccumulator> getProjectedPaymentAccumulation() {
    return getProjectedPaymentAccumulation(DEFAULT_TAKE_NTH);
  }

  public List<PaymentAccumulator> getProjectedPaymentAccumulation(long takeNth) {
    if (projectedPaymentAccumulation == null) {
      final PaymentAccumulator accumulator = 
        new PaymentAccumulator.Builder()
          .amount(BigDecimal.ZERO)
          .principal(BigDecimal.ZERO)
          .interest(BigDecimal.ZERO)
          .build();
      final List<LoanAmortization.Payment> projectedPayments = getProjectedPayments();
      final TakeNth<PaymentAccumulator> filterOnNth = new TakeNth<>(takeNth);
      this.projectedPaymentAccumulation =
        projectedPayments.stream()
          .map(payment -> {
            accumulator.addPayment(payment);
            return new PaymentAccumulator.Builder()
                                  .amount(accumulator.getAmount())
                                  .principal(accumulator.getPrincipal())
                                  .interest(accumulator.getInterest())
                                  .build();
          })
          .filter(filterOnNth)
          .collect(Collectors.toList());
    }

    return this.projectedPaymentAccumulation;
  }
}