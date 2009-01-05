package org.loandb.persistence.dao.jpa;

import org.loandb.persistence.dao.ApplicationDao;
import org.loandb.persistence.model.Application;
import org.springframework.stereotype.Repository;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Repository
public class ApplicationDaoJpa extends GenericDaoJpa<Application, Long> implements ApplicationDao {

  public ApplicationDaoJpa() {
    super(Application.class);
  }
}
