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
  @Test
  public void testFinanceProjection() {

    final FinanceProjection projection = new FinanceProjection(MockedLoans.SAMPLE_LOANS);

    // show expected transition on transition.
    final List<LoanAmortization.Payment> payments = projection.getProjectedPayments();

    for (int i = MockedLoans.START; i < MockedLoans.END; i++) {
      assertTrue(payments.get(i).equals(MockedLoans.EXPECTED_PAYMENTS_TRANSITION_SECTION.get(i - MockedLoans.START)));
    }

    // show expected accumulations comparison.
    final List<FinanceProjection.PaymentAccumulator> accumulatedPayements = projection.getProjectedPaymentAccumulation();

    final BigDecimal total = accumulatedPayements.get(accumulatedPayements.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL.equals(total));

    // show expected accumulation on an individual loan.
    final List<FinanceProjection.PaymentAccumulator> accumulatedPaymentsOnLoanA = projection.getProjectedPaymentAccumulationOnIndividualLoan(MockedLoans.LOAN_A);
    final BigDecimal totalOnLoanA = accumulatedPaymentsOnLoanA.get(accumulatedPaymentsOnLoanA.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL_ON_LOAN_A.equals(totalOnLoanA));

  }
}
