package org.loandb.persistence.service.impl;

import org.loandb.persistence.dao.ApplicationDao;
import org.loandb.persistence.model.Applicant;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.model.Decision;
import org.loandb.persistence.service.DecisionService;
import org.loandb.persistence.types.DecisionType;
import org.loandb.persistence.types.DeclineReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Service
@Transactional
public class DecisionServiceImpl implements DecisionService {

  @Autowired
  public ApplicationDao applicationDao;

  public Decision getRiskDecision(Long id) {
    Application app = applicationDao.get(id);

    Decision decision = new Decision();
    Set<Long> approvedBorrowers = new HashSet<Long>();
    Set<Long> declinedBorrowers = new HashSet<Long>();
    Set<Long> referredBorrowers = new HashSet<Long>();
    for (Applicant applicant : app.getApplicants()) {
      int score = applicant.getCbrSummary().getCreditScore();
      //simple logic to determine decision type
      //TODO add more business rules
      if (score > 700) {
        //approve
        approvedBorrowers.add(applicant.getId());
      } else if (score > 600 && score < 650) {
        //approve refer
        referredBorrowers.add(applicant.getId());
      } else {
        //decline
        declinedBorrowers.add(applicant.getId());
      }
    }

    if (declinedBorrowers.size() > 0) {
      decision.setDecision(DecisionType.DECLINE);
      decision.setDeclineReason(DeclineReason.FICO_SCORE.getReason());
    }

    if (referredBorrowers.size() > 0) {
      decision.setDecision(DecisionType.APPROVE_REFER);
    }

    if (declinedBorrowers.size() == 0 && referredBorrowers.size() == 0) {
      decision.setDecision(DecisionType.APPROVE);
    }

    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS Z");
    Date date = new Date();
    String datetime = dateFormat.format(date);
    try {
      date = dateFormat.parse(datetime);
      decision.setDecisionedDate(date);
    } catch (ParseException e) {
      e.printStackTrace();//TODO: remove this
    }
    return decision;
  }

}
