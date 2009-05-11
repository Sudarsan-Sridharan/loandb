package org.loandb.persistence.service;

import org.loandb.persistence.model.Applicant;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.model.CreditBureauSummary;
import org.loandb.persistence.model.Decision;

import java.util.List;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public interface ApplicationService {
    public Application createApp(Application application);

    public Application updateApp(Application application);

    public Application getApp(Long id);

    public void deleteApp(Long id);

    public List<Application> getAll();

    public Applicant saveCreditResponse(Applicant applicant, CreditBureauSummary cbrSummary);

    public Application saveDecisionResponse(Application application, Decision decision);
}
