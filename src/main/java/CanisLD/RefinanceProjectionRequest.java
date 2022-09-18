package CanisLD;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefinanceProjectionRequest {

  public static enum ValidationStatusCode {
    OK,
    INVALID_VALUES,
  };
  
  @JsonProperty("currentLoan")
  private FinanceProjection.Loan currentLoan;

  @JsonProperty("refinanceLoan")
  private FinanceProjection.Loan refinanceLoan;

  @JsonProperty("takeNth")
  private long takeNth;

  public RefinanceProjectionRequest() {
    takeNth = FinanceProjection.DEFAULT_TAKE_NTH;
  }

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
  public RefinanceProjectionRequest setTakeNth(long takeNth) {
    this.takeNth = takeNth;
    return this;
  }
  public long getTakeNth() {
    return takeNth;
  }

  public ValidationStatusCode validate() {
    if (currentLoan == null 
      || refinanceLoan == null
      || takeNth < FinanceProjection.MINIMUM_TAKE_NTH
    ) {
      return ValidationStatusCode.INVALID_VALUES;
    }

    switch (currentLoan.validate()) {
      case INVALID_VALUES:
        return ValidationStatusCode.INVALID_VALUES;
      default:
        break;
    }

    switch (refinanceLoan.validate()) {
      case INVALID_VALUES:
        return ValidationStatusCode.INVALID_VALUES;
      default:
        break;
    }

    return ValidationStatusCode.OK;
  }

  private RefinanceProjectionRequest(Builder builder) {
    this.currentLoan = builder.currentLoan;
    this.refinanceLoan = builder.refinanceLoan;
    this.takeNth = builder.takeNth;
  }

  public static class Builder {
    private FinanceProjection.Loan currentLoan;
    private FinanceProjection.Loan refinanceLoan;
    private long takeNth;

    public Builder() {
      takeNth = FinanceProjection.DEFAULT_TAKE_NTH;
    }
    public Builder currentLoan(FinanceProjection.Loan currentLoan) {
      this.currentLoan = currentLoan;
      return this;
    }
    public Builder refinanceLoan(FinanceProjection.Loan refinanceLoan) {
      this.refinanceLoan = refinanceLoan;
      return this;
    }
    public Builder takeNth(long takeNth) {
      this.takeNth = takeNth;
      return this;
    }
    public RefinanceProjectionRequest build() {
      return new RefinanceProjectionRequest(this);
    }
  }
}