package CanisLD;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.List;

public class RefinanceProjectionHandler implements RequestHandler<RefinanceProjectionRequest, RefinanceProjectionResponse> {
   
  @Override
  public RefinanceProjectionResponse handleRequest(RefinanceProjectionRequest event, Context context)
  {
    final LambdaLogger logger = context.getLogger();
    int httpStatusCode = HttpStatusCode.OK;

    switch (event.validate()) {
      case INVALID_VALUES:
        logger.log("Invalid request values");
        httpStatusCode = HttpStatusCode.BAD_REQUEST;
        break;
      default:
        break;
    }

    if (httpStatusCode != HttpStatusCode.OK) {
      return new RefinanceProjectionResponse.Builder()
        .httpStatusCode(httpStatusCode)
        .build();
    }

    final List<FinanceProjection.Loan> withRefinanceLoans = List.of(
      event.getCurrentLoan(),
      event.getRefinanceLoan()
    );
    FinanceProjection projection = new FinanceProjection(withRefinanceLoans);

    RefinanceProjectionResponse response = 
      new RefinanceProjectionResponse.Builder()
        .httpStatusCode(httpStatusCode)
        .currentLoanProjection(projection.getProjectedPaymentAccumulationOnIndividualLoan(event.getCurrentLoan().getLabel(), event.getTakeNth()))
        .refinanceLoanProjection(projection.getProjectedPaymentAccumulation(event.getTakeNth()))
        .build();
    return response;
  }
}