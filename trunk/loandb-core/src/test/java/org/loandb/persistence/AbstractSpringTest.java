package org.loandb.persistence;

import org.loandb.persistence.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 11, 2009
 */
public abstract class AbstractSpringTest extends AbstractTransactionalDataSourceSpringContextTests {
  public AbstractSpringTest() {
    super.setDefaultRollback(false);
  }

  protected
  @PersistenceContext(unitName = "loandb")
  EntityManager entityManager;

  @Autowired
  protected ApplicationService applicationService;

  @Override
  protected String[] getConfigLocations() {
    setAutowireMode(AUTOWIRE_BY_NAME);
    return new String[]{
      "/spring/spring-config.xml",
      "/spring/spring-jpa-config.xml",
      "/spring/spring-resources.xml"
    };
  }
}
