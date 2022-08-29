package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class MockedLoans {
  public static final String LOAN_A = "Loan A";
  public static final String LOAN_B = "Loan B";

  public static final List<FinanceProjection.Loan> SAMPLE_LOANS = 
    List.of(
      new FinanceProjection.Loan.Builder()
        .label(LOAN_A)
        .start(0L)
        .end(69L)
        .cost(4000.00)
        .loanDetails(
          new LoanAmortization.Loan.Builder()
            .amount(500000.00)
            .interestRate(0.035)
            .paymentFrequency(12L)
            .numberOfTerms(30L)
            .build()
        )
        .build(),
      new FinanceProjection.Loan.Builder()
        .label(LOAN_B)
        .start(69L)
        .end(430L)
        .cost(0.0)
        .loanDetails(
          new LoanAmortization.Loan.Builder()
          .amount(440000.00)
          .interestRate(0.03)
          .paymentFrequency(12L)
          .numberOfTerms(30L)
          .build()
        )
      .build()
    );
    
  // 2 before transition payment, and two after
  public static final int START = 67;
  public static final int END = 71;
  public static final List<LoanAmortization.Payment> EXPECTED_PAYMENTS_TRANSITION_SECTION =
    List.of(
      // 68th payment
      new LoanAmortization.Payment.Builder()
        .amount(2245.22)
        .principal(956.44)
        .interest(1288.78)
        .balance(440911.25)
        .build(),
      // 69th payment
      new LoanAmortization.Payment.Builder()
        .amount(2245.22)
        .principal(959.23)
        .interest(1285.99)
        .balance(439952.02)
        .build(),
      // 70th payment
      new LoanAmortization.Payment.Builder()
        .amount(1855.06)
        .principal(755.06)
        .interest(1100.00)
        .balance(439244.94)
        .build(),
      // 71th payment
      new LoanAmortization.Payment.Builder()
        .amount(1855.06)
        .principal(756.95)
        .interest(1098.11)
        .balance(438488.00)
        .build()
    );

  
  // total cost.
  public static final BigDecimal TOTAL = BigDecimal.valueOf(2245.22 * 69 + 1855.06 * 360).setScale(2, RoundingMode.HALF_EVEN);
  public static final BigDecimal TOTAL_ON_LOAN_A = BigDecimal.valueOf(2245.22 * 360).setScale(2, RoundingMode.HALF_EVEN);  
}
