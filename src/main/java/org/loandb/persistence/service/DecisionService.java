package org.loandb.persistence.service;

import org.loandb.persistence.model.Decision;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public interface DecisionService {
  public Decision getRiskDecision(Long dealId);
}
