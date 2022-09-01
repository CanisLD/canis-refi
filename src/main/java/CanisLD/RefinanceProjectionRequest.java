package CanisLD;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefinanceProjectionRequest {
  
  @JsonProperty("currentLoan")
  private FinanceProjection.Loan currentLoan;

  @JsonProperty("refinanceLoan")
  private FinanceProjection.Loan refinanceLoan;


  public RefinanceProjectionRequest() {}

  public RefinanceProjectionRequest setCurrentLoan(FinanceProjection.Loan currentLoan) {
    this.currentLoan = currentLoan;
    return this;
  }
  public FinanceProjection.Loan getCurrentLoan() {
    return currentLoan;
  }
  public RefinanceProjectionRequest setRefinanceLoan(FinanceProjection.Loan refinanceLoan) {
    this.refinanceLoan = refinanceLoan;
    return this;
  }
  public FinanceProjection.Loan getRefinanceLoan() {
    return refinanceLoan;
  }
  private RefinanceProjectionRequest(Builder builder) {
    this.currentLoan = builder.currentLoan;
    this.refinanceLoan = builder.refinanceLoan;
  }

  public static class Builder {
    private FinanceProjection.Loan currentLoan;
    private FinanceProjection.Loan refinanceLoan;

    public Builder currentLoan(FinanceProjection.Loan currentLoan) {
      this.currentLoan = currentLoan;
      return this;
    }
    public Builder refinanceLoan(FinanceProjection.Loan refinanceLoan) {
      this.refinanceLoan = refinanceLoan;
      return this;
    }
    public RefinanceProjectionRequest build() {
      return new RefinanceProjectionRequest(this);
    }
  }
}