package CanisLD;

import java.util.List;

public class RefinanceProjectionResponse {
  private final List<FinanceProjection.PaymentAccumulator> currentLoanProjection;
  private final List<FinanceProjection.PaymentAccumulator> refinanceLoanProjection;

  public List<FinanceProjection.PaymentAccumulator> getCurrentLoanProjection() {
    return currentLoanProjection;
  }
  public List<FinanceProjection.PaymentAccumulator> getRefinanceLoanProjection() {
    return refinanceLoanProjection;
  }
  private RefinanceProjectionResponse(Builder builder) {
    this.currentLoanProjection = builder.currentLoanProjection;
    this.refinanceLoanProjection = builder.refinanceLoanProjection;
  }

  public static class Builder {
    private List<FinanceProjection.PaymentAccumulator> currentLoanProjection;
    private List<FinanceProjection.PaymentAccumulator> refinanceLoanProjection;

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