package CanisLD;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class LoanAmortizationTest
{
  private final LoanAmortization loanAmortization;

  private final Map<String, LoanAmortization.Loan> sampleLoans;
  private final Map<String, List<LoanAmortization.Payment>> sampleLoanAmortizations;

  private final String FIXED_30Y_3p5APR_500K = "Fixed 30Y 3.5% APR 500K";

  public LoanAmortizationTest() {
    loanAmortization = new LoanAmortization();

    sampleLoans = new HashMap<>();
    sampleLoans.put(FIXED_30Y_3p5APR_500K, 
                    new LoanAmortization.Loan.Builder()
                      .amount(Double.valueOf(500000.00))
                      .interestRate(Double.valueOf(0.035))
                      .paymentFrequency(12L)
                      .numberOfTerms(30L)
                      .build());
    
    // show only the first few.
    sampleLoanAmortizations = new HashMap<>();
    sampleLoanAmortizations.put(FIXED_30Y_3p5APR_500K,
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
                                            .build()));
  }

  @Test
  public void testLoanAmortization() {
    for (Map.Entry<String, LoanAmortization.Loan> entry : sampleLoans.entrySet()) {
      
      LoanAmortization.Loan loan = entry.getValue();

      List<LoanAmortization.Payment> payments = loanAmortization.getAmortization(loan);

      // test number of payments.
      assertTrue(payments.size() == loan.getNumberOfTems() * loan.getPaymentFrequency());

      // test final balance value
      LoanAmortization.Payment lastPayment = payments.get(payments.size() - 1);
      assertTrue(lastPayment.scaledValue(lastPayment.getBalance()).equals(BigDecimal.valueOf(0).setScale(2)));

      // test first few amortization values.
      List<LoanAmortization.Payment> expectedLoanAmortization = sampleLoanAmortizations.get(entry.getKey());
      for (int index = 0; index < expectedLoanAmortization.size(); index++) {
        LoanAmortization.Payment expectedPayment = expectedLoanAmortization.get(index);
        LoanAmortization.Payment generatedPayment = payments.get(index);
        assertTrue(expectedPayment.equals(generatedPayment));
      }
    }
  }
}
