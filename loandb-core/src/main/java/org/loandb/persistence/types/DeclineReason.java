package org.loandb.persistence.types;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public enum DeclineReason {

  FICO_SCORE("FICO Score");

  private String reason;

  DeclineReason(String reason) {
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }
}
