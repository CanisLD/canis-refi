package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MockedLoans {
  public static final String LOAN_A = "Loan A";
  public static final String LOAN_B = "Loan B";

  public static final LoanAmortization.Loan SAMPLE_LOAN_DETAILS_A =
    new LoanAmortization.Loan.Builder()
    .amount(500000.00)
    .interestRate(0.035)
    .paymentFrequency(12L)
    .numberOfTerms(30L)
    .build();

  public static final FinanceProjection.Loan SAMPLE_LOAN_A =
    new FinanceProjection.Loan.Builder()
    .label(LOAN_A)
    .start(0L)
    .end(69L)
    .cost(4000.00)
    .loanDetails(SAMPLE_LOAN_DETAILS_A)
    .build();
  
  public static final FinanceProjection.Loan SAMPLE_LOAN_B =
    new FinanceProjection.Loan.Builder()
    .label(LOAN_B)
    .start(69L)
    .end(430L)
    .cost(5000.0)
    .loanDetails(
      new LoanAmortization.Loan.Builder()
      .amount(440000.00)
      .interestRate(0.03)
      .paymentFrequency(12L)
      .numberOfTerms(30L)
      .build()
    )
  .build();

  public static final List<FinanceProjection.Loan> SAMPLE_LOANS = List.of(SAMPLE_LOAN_A, SAMPLE_LOAN_B);

  // show only the first few.
  public static final List<LoanAmortization.Payment> EXPECTED_PAYEMENTS_HEAD =
    List.of(new LoanAmortization.Payment.Builder()
                .amount(2245.22)
                .principal(786.89)
                .interest(1458.33)
                .balance(499213.11)
                .build(),
            new LoanAmortization.Payment.Builder()
                .amount(2245.22)
                .principal(789.19)
                .interest(1456.04)
                .balance(498423.92)
                .build(),
            new LoanAmortization.Payment.Builder()
                .amount(2245.22)
                .principal(791.49)
                .interest(1453.74)
                .balance(497632.44)
                .build(),
            new LoanAmortization.Payment.Builder()
                .amount(2245.22)
                .principal(793.80)
                .interest(1451.43)
                .balance(496838.64)
                .build(),
            new LoanAmortization.Payment.Builder()
                .amount(2245.22)
                .principal(796.11)
                .interest(1449.11)
                .balance(496042.53)
                .build());
    
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
  public static final BigDecimal TOTAL = BigDecimal.valueOf(2245.22 * 69 + 1855.06 * 360 + 4000.0 + 5000.0).setScale(2, RoundingMode.HALF_EVEN);
  public static final BigDecimal TOTAL_ON_LOAN_A = BigDecimal.valueOf(2245.22 * 360 + 4000.0).setScale(2, RoundingMode.HALF_EVEN);
}
