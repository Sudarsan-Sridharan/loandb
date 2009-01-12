package org.loandb.persistence.model;

import org.loandb.persistence.types.LoanType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Entity
@XmlRootElement
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
public class Application extends BaseEntity {

  @Version
  @Column(name = "OBJ_VERSION")
  @XmlTransient
  private int version;

  @Column(name = "SUBMIT_DATE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date submitDate;

  @Column(name = "LOAN_AMT", nullable = false)
  private Double loanAmount;

  @Column(name = "LOAN_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private LoanType loanType;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "PROP_ADDRESS_ID", nullable = false)
  private Address propertyAddress;

  //  @OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "APPLICATION_ID", nullable = false)
  private Set<Applicant> applicants = new HashSet<Applicant>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "DECISION_ID")
  private Decision decision;

  public Double getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(Double loanAmount) {
    this.loanAmount = loanAmount;
  }

  public LoanType getLoanType() {
    return loanType;
  }

  public void setLoanType(LoanType loanType) {
    this.loanType = loanType;
  }

  public Address getPropertyAddress() {
    return propertyAddress;
  }

  public void setPropertyAddress(Address propertyAddress) {
    this.propertyAddress = propertyAddress;
  }

  public Set<Applicant> getApplicants() {
    return applicants;
  }

  public void setApplicants(Set<Applicant> applicants) {
    this.applicants = applicants;
  }

  public void addApplicant(Applicant applicant) {
    applicant.setApplication(this);
    applicants.add(applicant);
  }

  public Decision getDecision() {
    return decision;
  }

  public void setDecision(Decision decision) {
    this.decision = decision;
  }

  public int getVersion() {
    return version;
  }

  public Date getSubmitDate() {
    return submitDate;
  }

  public void setSubmitDate(Date submitDate) {
    this.submitDate = submitDate;
  }

  @Override
  public String toString() {
    return "Application{" +
      "version=" + version +
      ", submitDate=" + submitDate +
      ", loanAmount=" + loanAmount +
      ", loanType=" + loanType +
      ", propertyAddress=" + propertyAddress +
      ", applicants=" + applicants +
      ", decision=" + decision +
      '}';
  }
}
