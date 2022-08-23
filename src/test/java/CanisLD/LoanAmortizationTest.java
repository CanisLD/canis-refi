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
        .amount(Double.valueOf(500000.00))
        .interestRate(Double.valueOf(0.035))
        .paymentFrequency(12L)
        .numberOfTerms(30L)
        .build();
    
    // show only the first few.
    expectedLoanAmortization =
      List.of(new LoanAmortization.Payment.Builder()
                  .amount(Double.valueOf(2245.22))
                  .principal(Double.valueOf(786.89))
                  .interest(Double.valueOf(1458.33))
                  .balance(Double.valueOf(499213.11))
                  .build(),
              new LoanAmortization.Payment.Builder()
                  .amount(Double.valueOf(2245.22))
                  .principal(Double.valueOf(789.19))
                  .interest(Double.valueOf(1456.04))
                  .balance(Double.valueOf(498423.92))
                  .build(),
              new LoanAmortization.Payment.Builder()
                  .amount(Double.valueOf(2245.22))
                  .principal(Double.valueOf(791.49))
                  .interest(Double.valueOf(1453.74))
                  .balance(Double.valueOf(497632.44))
                  .build(),
              new LoanAmortization.Payment.Builder()
                  .amount(Double.valueOf(2245.22))
                  .principal(Double.valueOf(793.80))
                  .interest(Double.valueOf(1451.43))
                  .balance(Double.valueOf(496838.64))
                  .build(),
              new LoanAmortization.Payment.Builder()
                  .amount(Double.valueOf(2245.22))
                  .principal(Double.valueOf(796.11))
                  .interest(Double.valueOf(1449.11))
                  .balance(Double.valueOf(496042.53))
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
    assertTrue(lastPayment.scaledValue(lastPayment.getBalance()).equals(BigDecimal.valueOf(0).setScale(2)));

    // test first few amortization values.
    for (int index = 0; index < expectedLoanAmortization.size(); index++) {
      LoanAmortization.Payment expectedPayment = expectedLoanAmortization.get(index);
      LoanAmortization.Payment generatedPayment = payments.get(index);
      assertTrue(expectedPayment.equals(generatedPayment));
    }
  }
}
