package org.loandb.persistence.model;

import org.loandb.persistence.types.DecisionType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Entity
@XmlRootElement
public class Decision extends BaseEntity {
  @Column(name = "DEC_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private DecisionType decision;

  @Column(name = "DEC_DATE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date decisionedDate;

  @OneToOne(mappedBy = "decision")
  private Application application;

  public DecisionType getDecision() {
    return decision;
  }

  public void setDecision(DecisionType decision) {
    this.decision = decision;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public Date getDecisionedDate() {
    return decisionedDate;
  }

  public void setDecisionedDate(Date decisionedDate) {
    this.decisionedDate = decisionedDate;
  }

  @Override
  public String toString() {
    return "Decision{" +
      "decision=" + decision +
      ", decisionedDate=" + decisionedDate +
      ", application=" + application +
      '}';
  }
}
