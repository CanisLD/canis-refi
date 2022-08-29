package CanisLD;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RefinanceProjectionHandlerTest {
  
  @Test
  public void testRefinanceProjectionHandler() {

    final RefinanceProjectionHandler handler = new RefinanceProjectionHandler();
    final RefinanceProjectionRequest request = 
      new RefinanceProjectionRequest.Builder()
        .currentLoan(MockedLoans.SAMPLE_LOANS.get(0))
        .refinanceLoan(MockedLoans.SAMPLE_LOANS.get(1))
        .build();
    final RefinanceProjectionResponse response = handler.handleRequest(request, null);
    assertTrue(response != null);

    final List<FinanceProjection.PaymentAccumulator> accumulatedPaymentsOnCurrentLoan = response.getCurrentLoanProjection();
    final BigDecimal totalOnCurrentLoan = accumulatedPaymentsOnCurrentLoan.get(accumulatedPaymentsOnCurrentLoan.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL_ON_LOAN_A.equals(totalOnCurrentLoan));

    List<FinanceProjection.PaymentAccumulator> accumulatedPayements = response.getRefinanceLoanProjection();
    final BigDecimal total = accumulatedPayements.get(accumulatedPayements.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL.equals(total));
  }
}
