package org.loandb.persistence;

import org.junit.runner.RunWith;
import org.loandb.persistence.service.ApplicationService;
import org.loandb.persistence.service.CreditService;
import org.loandb.persistence.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager")
@TestExecutionListeners({TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:/spring/spring-config.xml",
        "classpath:/spring/spring-jpa-config.xml",
        "classpath:/spring/spring-resources.xml"})
public abstract class AbstractTest extends AbstractTransactionalJUnit4SpringContextTests {
    @PersistenceContext
    protected EntityManager entityManager;

    @PersistenceUnit(name = "loandb")
    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected CreditService creditService;

    @Autowired
    protected DecisionService decisionService;
}
