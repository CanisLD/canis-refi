package CanisLD;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RefinanceProjectionHandler implements RequestHandler<RefinanceProjectionRequest, RefinanceProjectionResponse> {
  
  private Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  @Override
  public RefinanceProjectionResponse handleRequest(RefinanceProjectionRequest event, Context context)
  {
    // LambdaLogger logger = context.getLogger();
    // String response = "200 OK";
    // // log execution details
    // logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
    // logger.log("CONTEXT: " + gson.toJson(context));
    // // process event
    // logger.log("EVENT: " + gson.toJson(event));
    // logger.log("EVENT TYPE: " + event.getClass());

    final List<FinanceProjection.Loan> loans = Collections.emptyList();
    FinanceProjection financeProjection = new FinanceProjection(loans);

    RefinanceProjectionResponse response = 
      new RefinanceProjectionResponse.Builder()
        .build();
    return response;
  }
}