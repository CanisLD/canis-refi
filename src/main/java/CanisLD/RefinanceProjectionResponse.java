package CanisLD;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefinanceProjectionResponse {

  @JsonProperty("httpStatusCode")
  private final int httpStatusCode;

  @JsonProperty("currentLoanProjection")
  private final List<FinanceProjection.PaymentAccumulator> currentLoanProjection;

  @JsonProperty("refinanceLoanProjection")
  private final List<FinanceProjection.PaymentAccumulator> refinanceLoanProjection;

  public int getHttpStatusCode() {
    return httpStatusCode;
  }
  public List<FinanceProjection.PaymentAccumulator> getCurrentLoanProjection() {
    return currentLoanProjection;
  }
  public List<FinanceProjection.PaymentAccumulator> getRefinanceLoanProjection() {
    return refinanceLoanProjection;
  }
  private RefinanceProjectionResponse(Builder builder) {
    this.httpStatusCode = builder.httpStatusCode;
    this.currentLoanProjection = builder.currentLoanProjection;
    this.refinanceLoanProjection = builder.refinanceLoanProjection;
  }

  public static class Builder {
    private int httpStatusCode;
    private List<FinanceProjection.PaymentAccumulator> currentLoanProjection;
    private List<FinanceProjection.PaymentAccumulator> refinanceLoanProjection;

    public Builder httpStatusCode(int httpStatusCode) {
      this.httpStatusCode = httpStatusCode;
      return this;
    } 
    public Builder currentLoanProjection(List<FinanceProjection.PaymentAccumulator> currentLoanProjection) {
      this.currentLoanProjection = currentLoanProjection;
      return this;
    }
    public Builder refinanceLoanProjection(List<FinanceProjection.PaymentAccumulator> refinanceLoanProjection) {
      this.refinanceLoanProjection = refinanceLoanProjection;
      return this;
    }
    public RefinanceProjectionResponse build() {
      return new RefinanceProjectionResponse(this);
    }
  }
}