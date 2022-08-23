package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jdk.internal.jshell.tool.resources.version;

public class LoanAmortization {

  public static class Payment {
    private final Double amount;
    private final Double principal;
    private final Double interest;
    private final Double balance;

    public Double getAmount() {
      return amount;
    }
    public Double getPrincipal() {
      return principal;
    }
    public Double getInterest() {
      return interest;
    }
    public Double getBalance() {
      return balance;
    }
    private Payment(Builder builder) {
      amount = builder.amount;
      principal = builder.principal;
      interest = builder.interest;
      balance = builder.balance;
    }
    @Override
    public String toString() {
      return new StringBuilder("{")
        .append("amount:").append(amount.toString()).append(",")
        .append("principal:").append(principal.toString()).append(",")
        .append("interest:").append(interest.toString()).append(",")
        .append("balance:").append(balance.toString()).append(",")
        .append("}")
        .toString();
    }
    @Override
    public boolean equals(Object other) {
      // self check
      if (this == other) {
          return true;
      }
      // null check
      if (other == null) {
          return false;
      }

      // type check and cast
      if (getClass() != other.getClass()) {
          return false;
      }

      Payment payment = (Payment) other;

      return this.scaledEquals(payment);
    }
    public BigDecimal scaledValue(double value) {
      return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_EVEN);
    }
    public boolean scaledEquals(Payment other) {
      return scaledValue(amount).equals(scaledValue(other.amount))
        && scaledValue(principal).equals(scaledValue(other.principal))
        && scaledValue(interest).equals(scaledValue(other.interest))
        && scaledValue(balance).equals(scaledValue(other.balance));
    }
    public static class Builder {
      private Double amount;
      private Double principal;
      private Double interest;
      private Double balance;

      public Builder amount(Double amount) {
        this.amount = amount;
        return this;
      }
      public Builder principal(Double principal) {
        this.principal = principal;
        return this;
      }
      public Builder interest(Double interest) {
        this.interest = interest;
        return this;
      }
      public Builder balance(Double balance) {
        this.balance = balance;
        return this;
      }
      public Payment build() {
        return new Payment(this);
      }
    }
  }

  public static class Loan {
    private final Double interestRate;
    private final Double amount;
    private final Long paymentFrequency;
    private final Long numberOfTerms;   
  
    public Double getInterestRate() {
      return interestRate;
    }
    public Double getAmount() {
      return amount;
    }
    public Long getPaymentFrequency() {
      return paymentFrequency;
    }
    public Long getNumberOfTems() {
      return numberOfTerms;
    }
    protected Loan(Builder builder) {
      interestRate = builder.interestRate;
      amount = builder.amount;
      paymentFrequency = builder.paymentFrequency;
      numberOfTerms = builder.numberOfTerms;
    }
    public static class Builder {
      private Double interestRate;
      private Double amount;
      private Long paymentFrequency;
      private Long numberOfTerms;   

      public Builder interestRate(Double interestRate) {
        this.interestRate = interestRate;
        return this;
      }
      public Builder amount(Double amount) {
        this.amount = amount;
        return this;
      }
      public Builder paymentFrequency(Long paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
        return this;
      }
      public Builder numberOfTerms(Long numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
        return this;
      }
      public boolean isValid() {
        return interestRate != null
          && amount != null 
          && paymentFrequency != null 
          && numberOfTerms != null 
          && paymentFrequency.longValue() > 0
          && numberOfTerms.longValue() > 0;
      }
      public Loan build() {
        if (!isValid()) {
          throw new RuntimeException("Loan data missing critical values");
        }
        return new Loan(this);
      }
    }
  }

  public double calcPerPaymentInterestRate(double interestRate, double paymentFrequency) {
    return interestRate / paymentFrequency;
  }

  public long calcTotalNumberOfPayments(long paymentFrequency, long numberOfTerms) {
    return paymentFrequency * numberOfTerms;
  }
  
  public double calcPerPaymentPrincipal(
    double payment,
    double outstandingLoanBalance,
    double perPaymentInterestRate 
  ) {
    return payment - (outstandingLoanBalance * perPaymentInterestRate);
  }

  // Payment = (P x (r / n) x (1 + r / n)^n(t)]) / ((1 + r / n)^n(t) - 1)
  public double calcAmortizedPayment(
    double loanAmount,
    double perPaymentInterestRate,
    double totalNumberOfPayments
  ) {
    double loanTermInterestRate = Math.pow(1 + perPaymentInterestRate, totalNumberOfPayments);
    return loanAmount * ((perPaymentInterestRate * loanTermInterestRate) / (loanTermInterestRate - 1));
  }

  public Payment getPaymentInstallation(
    double amortizedPayment,
    double outstandingLoanBalance,
    double perPaymentInterestRate
  ) {
    double perPaymentPrincipal = calcPerPaymentPrincipal(amortizedPayment, outstandingLoanBalance, perPaymentInterestRate);
    return new Payment.Builder()
      .amount(Double.valueOf(amortizedPayment))
      .principal(Double.valueOf(perPaymentPrincipal))
      .balance(Double.valueOf(outstandingLoanBalance - perPaymentPrincipal))
      .interest(Double.valueOf(amortizedPayment - perPaymentPrincipal))
      .build();
  }

  private final Loan loan;
  private List<Payment> amortization;

  public LoanAmortization(Loan loan) {
    this.loan = loan;
  }

  public Loan getLoan() {
    return loan;
  }

  public List<Payment> getAmortization() {
    if (amortization == null) {
      amortization = getAmortization(loan);
    }

    return amortization;
  }

  public List<Payment> getAmortization(Loan loan) {

    final double interestRate = loan.getInterestRate().doubleValue();
    final double loanAmount = loan.getAmount().doubleValue();
    final long paymentFrequency = loan.getPaymentFrequency();
    final long numberOfTerms = loan.getNumberOfTems();
    final long totalNumberOfPayments = calcTotalNumberOfPayments(paymentFrequency, numberOfTerms);
    final double perPaymentInterestRate = calcPerPaymentInterestRate(interestRate, paymentFrequency);
    final double amortizedPayment = calcAmortizedPayment(loanAmount, perPaymentInterestRate, totalNumberOfPayments);

    final ArrayList<Payment> amortizedPaymentList = new ArrayList<>();
    double outstandingLoanBalance = loanAmount;
    for (int i = 0; i < totalNumberOfPayments; i++) {
      final Payment payment = getPaymentInstallation(amortizedPayment, outstandingLoanBalance, perPaymentInterestRate);
      amortizedPaymentList.add(payment);
      outstandingLoanBalance = payment.getBalance().doubleValue();
    }

    return amortizedPaymentList;
  }
}