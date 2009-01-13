package org.loandb.persistence.service.impl;

import org.loandb.persistence.model.CreditBureauSummary;
import org.loandb.persistence.service.CreditService;
import org.loandb.persistence.types.CreditBureauType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Service
@Transactional
public class CreditServiceImpl implements CreditService {
  private final static Logger logger = LoggerFactory.getLogger(CreditServiceImpl.class);

  public CreditBureauSummary getCredit(Long id) {
    logger.debug("Getting credit for application id {}", id);
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS Z");
    java.util.Date date = new java.util.Date();
    String datetime = dateFormat.format(date);
    CreditBureauSummary cbr = new CreditBureauSummary();
    cbr.setCreditBureau(getCreditBureau());
    try {
      date = dateFormat.parse(datetime);
      cbr.setRequestedDate(date);
    } catch (ParseException e) {
      e.printStackTrace();//TODO remove this
    }
    cbr.setCreditScore(getFicoScore(cbr.getCreditBureau()));
    logger.debug("Credit received for applicant id {} with score : {}", id, cbr.getCreditScore());
    return cbr;
  }

  public List<CreditBureauSummary> getCredits(List<Long> applicantIds) {
    //TODO: business rule for a multi-borrower scenario may be different.
    //TODO: probably use the same credit bureau
    List<CreditBureauSummary> creditSummaries = new ArrayList<CreditBureauSummary>();
    for (Long id : applicantIds) {
      creditSummaries.add(getCredit(id));
    }
    return creditSummaries;
  }

  //  FICO score range (personal report vs lenders)
  //  Experian is 330 - 830 vs. 300 - 850
  //  TransUnion is 300 - 850 vs. 336 - 843
  //  Equifax is 300 - 850 vs. 300 - 850
  private int getFicoScore(CreditBureauType creditBureau) {
    int min = 0, max = 0;
    if (creditBureau.equals(CreditBureauType.EXPERIAN) || creditBureau.equals(CreditBureauType.EQUIFAX)) {
      min = 300;
      max = 850;
    } else {
      min = 336;
      max = 843;
    }

    Random rnd = new Random();
    return rnd.nextInt(max - min + 1) + min;
  }

  private CreditBureauType getCreditBureau() {
    Random rnd = new Random();
    Object[] objs = EnumSet.allOf(CreditBureauType.class).toArray();
    return (CreditBureauType) objs[rnd.nextInt(objs.length)];
  }
}
