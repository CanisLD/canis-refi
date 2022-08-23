package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for FinanceProjection class.
 */
public class FinanceProjectionTest
{
  private final String LOAN_A = "Loan A";
  private final String LOAN_B = "Loan B";
  private final List<FinanceProjection.Loan> sampleLoans;

  // 2 before transition payment, and two after
  private final int START = 67;
  private final int END = 71;
  private final List<LoanAmortization.Payment> expectedPaymentTransitions;

  // total cost.
  private final BigDecimal TOTAL = BigDecimal.valueOf(2245.22 * 69 + 1855.06 * 360).setScale(2, RoundingMode.HALF_EVEN);

  public FinanceProjectionTest() {

    sampleLoans = List.of(
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

    expectedPaymentTransitions = List.of(
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
  }

  @Test
  public void testFinanceProjection() {

    final FinanceProjection projection = new FinanceProjection(this.sampleLoans);

    // show expected transition on transition.
    final List<LoanAmortization.Payment> payments = projection.getProjectedPayments();

    for (int i = START; i < END; i++) {
      assertTrue(payments.get(i).equals(expectedPaymentTransitions.get(i - START)));
    }

    // show expected accumulations comparison.
    final List<FinanceProjection.PaymentAccumulator> accumulatedPayements = projection.getProjectedPaymentAccumulation();

    final BigDecimal total = accumulatedPayements.get(accumulatedPayements.size() - 1).getAmount();
    assertTrue(TOTAL.equals(total));
  }
}