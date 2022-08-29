package CanisLD;

public class RefinanceProjectionRequest {
  private final FinanceProjection.Loan currentLoan;
  private final FinanceProjection.Loan refinanceLoan;

  // public RefinanceProjectionRequest setCurrentLoan(FinanceProjection.Loan currentLoan) {
  //   this.currentLoan = currentLoan;
  //   return this;
  // }
  // public RefinanceProjectionRequest setRefinanceLoan(FinanceProjection.Loan refinanceLoan) {
  //   this.refinanceLoan = refinanceLoan;
  //   return this;
  // }
  public FinanceProjection.Loan getCurrentLoan() {
    return currentLoan;
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