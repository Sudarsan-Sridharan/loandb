package org.loandb.persistence.dao.jpa;

import org.loandb.persistence.dao.ApplicantDao;
import org.loandb.persistence.model.Applicant;
import org.springframework.stereotype.Repository;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Repository
public class ApplicantDaoJpa extends GenericDaoJpa<Applicant, Long> implements ApplicantDao {
  public ApplicantDaoJpa() {
    super(Applicant.class);
  }
}
