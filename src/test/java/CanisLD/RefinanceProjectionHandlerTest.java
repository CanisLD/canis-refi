package CanisLD;

import java.math.BigDecimal;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class RefinanceProjectionHandlerTest {

  @Test
  public void testRefinanceProjectionHandler() {

    LambdaLogger mockedLogger = Mockito.mock(LambdaLogger.class);
    Context mockedContext = Mockito.mock(Context.class);
    Mockito.when(mockedContext.getLogger()).thenReturn(mockedLogger);

    final RefinanceProjectionHandler handler = new RefinanceProjectionHandler();
    final RefinanceProjectionRequest request = 
      new RefinanceProjectionRequest.Builder()
        .currentLoan(MockedLoans.SAMPLE_LOAN_A)
        .refinanceLoan(MockedLoans.SAMPLE_LOAN_B)
        .build();

    assertTrue(request.validate() == RefinanceProjectionRequest.ValidationStatusCode.OK);
    
    final RefinanceProjectionResponse response = handler.handleRequest(request, mockedContext);
    assertTrue(response != null);

    List<FinanceProjection.PaymentAccumulator> accumulatedPayements = response.getRefinanceLoanProjection();
    final BigDecimal total = accumulatedPayements.get(accumulatedPayements.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL.equals(total));

    final List<FinanceProjection.PaymentAccumulator> accumulatedPaymentsOnCurrentLoan = response.getCurrentLoanProjection();
    final BigDecimal totalOnCurrentLoan = accumulatedPaymentsOnCurrentLoan.get(accumulatedPaymentsOnCurrentLoan.size() - 1).getAmount();
    assertTrue(MockedLoans.TOTAL_ON_LOAN_A.equals(totalOnCurrentLoan));
  }
}
