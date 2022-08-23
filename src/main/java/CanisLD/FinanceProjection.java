package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceProjection {

  public static class PaymentAccumulator {
    
    private BigDecimal amount;
    private BigDecimal principal;
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
    private final String label;
      
    // the starting period for finance segment
    private final Long start;

    // the ending period for finance segment
    private final Long end;

    // upfront const on securing the loans. fees, points, etc.
    private final Double cost;

    // amortized loan
    private final LoanAmortization.Loan loanDetails;
  
    public String getLabel() {
      return label;
    }
    public Long getStart() {
      return start;
    }
    public Long getEnd() {
      return end;
    }
    public Double getCost() {
      return cost;
    }
    public LoanAmortization.Loan getLoanAmortizationDetails() {
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
      private Long start;
      private Long end;
      private Double cost;
      private LoanAmortization.Loan loanDetails;

      public Builder label(String label) {
        this.label = label;
        return this;
      }
      public Builder start(Long start) {
        this.start = start;
        return this;
      }
      public Builder end(Long end) {
        this.end = end;
        return this;
      }
      public Builder cost(Double cost) {
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

  private final Map<String, LoanAmortization> loanAmortizations;

  private List<LoanAmortization.Payment> projectedPayments;

  private List<PaymentAccumulator> projectedPaymentAccumulation;

  public FinanceProjection(List<Loan> loans) {
    this.loans = loans;
    this.loanAmortizations = 
      loans.stream()
           .collect(Collectors.toMap(
                      loan -> loan.getLabel(), 
                      loan -> new LoanAmortization(loan.getLoanAmortizationDetails())));
  }

  public List<Loan> getLoans() {
    return loans;
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

  public List<PaymentAccumulator> getProjectedPaymentAccumulation() {
    if (projectedPaymentAccumulation == null) {
      final PaymentAccumulator accumulator = 
        new PaymentAccumulator.Builder()
          .amount(BigDecimal.ZERO)
          .principal(BigDecimal.ZERO)
          .interest(BigDecimal.ZERO)
          .build();
      final List<LoanAmortization.Payment> projectedPayments = getProjectedPayments();
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
          .collect(Collectors.toList());
    }

    return this.projectedPaymentAccumulation;
  }
}