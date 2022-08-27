package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoanAmortization {

  public static class Payment {
    private final double amount;
    private final double principal;
    private final double interest;
    private final double balance;

    public double getAmount() {
      return amount;
    }
    public double getPrincipal() {
      return principal;
    }
    public double getInterest() {
      return interest;
    }
    public double getBalance() {
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
        .append("amount:").append(amount).append(",")
        .append("principal:").append(principal).append(",")
        .append("interest:").append(interest).append(",")
        .append("balance:").append(balance).append(",")
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
      private double amount;
      private double principal;
      private double interest;
      private double balance;

      public Builder amount(double amount) {
        this.amount = amount;
        return this;
      }
      public Builder principal(double principal) {
        this.principal = principal;
        return this;
      }
      public Builder interest(double interest) {
        this.interest = interest;
        return this;
      }
      public Builder balance(double balance) {
        this.balance = balance;
        return this;
      }
      public Payment build() {
        return new Payment(this);
      }
    }
  }

  public static class Loan {
    private final double interestRate;
    private final double amount;
    private final long paymentFrequency;
    private final long numberOfTerms;   
  
    public double getInterestRate() {
      return interestRate;
    }
    public double getAmount() {
      return amount;
    }
    public long getPaymentFrequency() {
      return paymentFrequency;
    }
    public long getNumberOfTems() {
      return numberOfTerms;
    }
    protected Loan(Builder builder) {
      interestRate = builder.interestRate;
      amount = builder.amount;
      paymentFrequency = builder.paymentFrequency;
      numberOfTerms = builder.numberOfTerms;
    }
    public static class Builder {
      private double interestRate;
      private double amount;
      private long paymentFrequency;
      private long numberOfTerms;   

      public Builder interestRate(double interestRate) {
        this.interestRate = interestRate;
        return this;
      }
      public Builder amount(double amount) {
        this.amount = amount;
        return this;
      }
      public Builder paymentFrequency(long paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
        return this;
      }
      public Builder numberOfTerms(long numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
        return this;
      }
      public Loan build() {
        return new Loan(this);
      }
    }
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
      .amount(amortizedPayment)
      .principal(perPaymentPrincipal)
      .balance(outstandingLoanBalance - perPaymentPrincipal)
      .interest(amortizedPayment - perPaymentPrincipal)
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

    final double interestRate = loan.getInterestRate();
    final double loanAmount = loan.getAmount();
    final long paymentFrequency = loan.getPaymentFrequency();
    final long numberOfTerms = loan.getNumberOfTems();
    final long totalNumberOfPayments = paymentFrequency * numberOfTerms;
    final double perPaymentInterestRate = interestRate / paymentFrequency;
    final double amortizedPayment = calcAmortizedPayment(loanAmount, perPaymentInterestRate, totalNumberOfPayments);

    final ArrayList<Payment> amortizedPaymentList = new ArrayList<>();
    double outstandingLoanBalance = loanAmount;
    for (int i = 0; i < totalNumberOfPayments; i++) {
      final Payment payment = getPaymentInstallation(amortizedPayment, outstandingLoanBalance, perPaymentInterestRate);
      amortizedPaymentList.add(payment);
      outstandingLoanBalance = payment.getBalance();
    }

    return amortizedPaymentList;
  }
}