package CanisLD;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for LoanAmortization class.
 */
public class LoanAmortizationTest
{
  @Test
  public void testLoanAmortization() {
    final LoanAmortization.Loan sampleLoan = MockedLoans.SAMPLE_LOAN_DETAILS_A;
    final List<LoanAmortization.Payment> expectedLoanAmortization = MockedLoans.EXPECTED_PAYEMENTS_HEAD;

    LoanAmortization loanAmortization = new LoanAmortization(sampleLoan);
    List<LoanAmortization.Payment> payments = loanAmortization.getAmortization();

    // test number of payments.
    assertTrue(payments.size() == sampleLoan.getNumberOfTems() * sampleLoan.getPaymentFrequency());

    // test final balance value
    LoanAmortization.Payment lastPayment = payments.get(payments.size() - 1);
    assertTrue(lastPayment.scaledValue(lastPayment.getBalance()).equals(BigDecimal.ZERO.setScale(2)));

    // test first few amortization values.
    for (int index = 0; index < expectedLoanAmortization.size(); index++) {
      LoanAmortization.Payment expectedPayment = expectedLoanAmortization.get(index);
      LoanAmortization.Payment generatedPayment = payments.get(index);
      assertTrue(expectedPayment.equals(generatedPayment));
    }
  }
}
