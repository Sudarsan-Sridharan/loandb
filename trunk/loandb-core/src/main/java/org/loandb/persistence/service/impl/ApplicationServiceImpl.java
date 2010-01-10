package org.loandb.persistence.service.impl;

import org.loandb.persistence.dao.ApplicantDao;
import org.loandb.persistence.dao.ApplicationDao;
import org.loandb.persistence.model.Applicant;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.model.CreditBureauSummary;
import org.loandb.persistence.model.Decision;
import org.loandb.persistence.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    public ApplicationDao applicationDao;

    @Autowired
    public ApplicantDao applicantDao;

    public Application createApp(Application application) {
        LOG.debug("saving application for loan type " + application.getLoanType());
        return applicationDao.save(application);
    }

    public Application updateApp(Application application) {
        LOG.debug("updating application id {} " + application.getId());
        return applicationDao.update(application);
    }

    public Application getApp(Long id) {
        LOG.debug("retrieving application id {} " + id);
        return applicationDao.get(id);
    }

    public void deleteApp(Long id) {
        LOG.debug("deleting application id {} " + id);
        applicationDao.remove(id);
    }

    public List<Application> getAll() {
        return applicationDao.getAll();
    }

    public Applicant saveCreditResponse(Applicant applicant, CreditBureauSummary cbrSummary) {
        LOG.debug("saving credit response for applicant id {} " + applicant.getId());
        applicant.setCbrSummary(cbrSummary);
        cbrSummary.setApplicant(applicant);
        return applicantDao.update(applicant);
    }

    public Application saveDecisionResponse(Application application, Decision decision) {
        LOG.debug("saving decision for application id {} " + application.getId());
        application.setDecision(decision);
        decision.setApplication(application);
        return applicationDao.update(application);
    }

    public void clear() {
        LOG.debug("clearing all applications. use this for testing purposes only.");
        for (Application application : getAll()) {
            deleteApp(application.getId());
        }
    }
}
