package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinanceProjection {

  public static final long MINIMUM_TAKE_NTH = 1L;
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

  public static class CostChecker {
    private long index;
    private Loan currentLoan;
    private final Iterator<Loan> loansIterator;    

    public CostChecker(List<Loan> loans) {
      loansIterator = loans.iterator();
      currentLoan = loansIterator.hasNext() ? loansIterator.next() : null;
      index = 0;
    }

    public double getCost() {
      double cost = 0.0;
      if (currentLoan != null && currentLoan.getStart() == index) {
        cost = currentLoan.getCost();
        currentLoan = loansIterator.hasNext() ? loansIterator.next() : null;
      }
      index++;
      return cost;
    }
  }

  public static class PaymentAccumulator {
    
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("principal")
    private BigDecimal principal;

    @JsonProperty("interest")
    private BigDecimal interest;


    public PaymentAccumulator accumulate(double amount, double principal, double interest) {
      this.amount = this.amount.add(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN));
      this.principal = this.principal.add(BigDecimal.valueOf(principal).setScale(2, RoundingMode.HALF_EVEN));
      this.interest = this.interest.add(BigDecimal.valueOf(interest).setScale(2, RoundingMode.HALF_EVEN));
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

    public enum ValidationStatusCode {
      OK,
      INVALID_VALUES,
    }

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

    public ValidationStatusCode validate() {
      
      if (label == null
        || loanDetails == null
        || start < 0
        || end < 0
        || start > end
      ) {
        return ValidationStatusCode.INVALID_VALUES;
      }

      switch (loanDetails.validate()) {
        case INVALID_VALUES:
          return ValidationStatusCode.INVALID_VALUES;
        default:
          return ValidationStatusCode.OK;
      }
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
      final CostChecker costChecker = new CostChecker(List.of(loansByLabel.get(loan.getLabel())));
      return projectedPayments.stream()
        .map(payment -> {
          accumulator.accumulate(payment.getAmount() + costChecker.getCost(), payment.getPrincipal(), payment.getInterest());
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
      final CostChecker costChecker = new CostChecker(loans);
      this.projectedPaymentAccumulation =
        projectedPayments.stream()
          .map(payment -> {
            accumulator.accumulate(payment.getAmount() + costChecker.getCost(), payment.getPrincipal(), payment.getInterest());
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