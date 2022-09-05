package CanisLD;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for LoanAmortization class.
 */
public class LoanAmortizationTest
{
  private final LoanAmortization.Loan sampleLoan;
  private final List<LoanAmortization.Payment> expectedLoanAmortization;

  public LoanAmortizationTest() {

    sampleLoan = 
      new LoanAmortization.Loan.Builder()
        .amount(500000.00)
        .interestRate(0.035)
        .paymentFrequency(12L)
        .numberOfTerms(30L)
        .build();
    
    // show only the first few.
    expectedLoanAmortization =
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
  }

  @Test
  public void testLoanAmortization() {
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
