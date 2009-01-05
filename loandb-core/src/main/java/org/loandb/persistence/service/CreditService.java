package org.loandb.persistence.service;

import org.loandb.persistence.model.CreditBureauSummary;

import java.util.List;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public interface CreditService {
  public CreditBureauSummary getCredit(Long applicantId);

  public List<CreditBureauSummary> getCredits(List<Long> applicantIds);
}
