package CanisLD;

public class RefinanceProjectionRequest {
  private FinanceProjection.Loan currentLoan;
  private FinanceProjection.Loan refinanceLoan;

  public RefinanceProjectionRequest setCurrentLoan(FinanceProjection.Loan currentLoan) {
    this.currentLoan = currentLoan;
    return this;
  }
  public RefinanceProjectionRequest setRefinanceLoan(FinanceProjection.Loan refinanceLoan) {
    this.refinanceLoan = refinanceLoan;
    return this;
  }
  public FinanceProjection.Loan getCurrentLoan() {
    return currentLoan;
  }
  public FinanceProjection.Loan getRefinanceLoan() {
    return refinanceLoan;
  }
}