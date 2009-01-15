package org.loandb.persistence.model;

import org.hibernate.envers.RevisionListener;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 14, 2009
 */
public class DealListener implements RevisionListener {

  public void newRevision(Object revEntity) {
    DealRevision dealRev = (DealRevision) revEntity;
    //FIXME: This should come from the user submiting the deal.
    dealRev.setUsername("admin");
  }
}
