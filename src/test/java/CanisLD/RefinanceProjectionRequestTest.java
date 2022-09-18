package CanisLD;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RefinanceProjectionRequestTest {
  
  @Test
  public void testMissingRequestValues() {
    RefinanceProjectionRequest request = new RefinanceProjectionRequest.Builder()
      .build();
    assertTrue(request.validate() == RefinanceProjectionRequest.ValidationStatusCode.INVALID_VALUES);
  }

  @Test
  public void testBadTakeNthValue() {
    RefinanceProjectionRequest request = new RefinanceProjectionRequest.Builder()
      .currentLoan(MockedLoans.SAMPLE_LOAN_A)
      .refinanceLoan(MockedLoans.SAMPLE_LOAN_B)
      .takeNth(-1L)
      .build();
    assertTrue(request.validate() == RefinanceProjectionRequest.ValidationStatusCode.INVALID_VALUES);
  }

  @Test
  public void testValidRequest() {
    RefinanceProjectionRequest request = new RefinanceProjectionRequest.Builder()
      .currentLoan(MockedLoans.SAMPLE_LOAN_A)
      .refinanceLoan(MockedLoans.SAMPLE_LOAN_B)
      .takeNth(FinanceProjection.MINIMUM_TAKE_NTH)
      .build();
    assertTrue(request.validate() == RefinanceProjectionRequest.ValidationStatusCode.OK);
  }
}
