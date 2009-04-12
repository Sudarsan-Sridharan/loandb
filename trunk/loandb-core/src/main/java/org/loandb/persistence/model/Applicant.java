package org.loandb.persistence.model;

import org.hibernate.envers.Audited;
import org.loandb.persistence.types.ApplicantRole;

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
public class Applicant extends BaseEntity {
  @Audited
  @Column(name = "SSN", nullable = false)
  private String ssn;

  @Audited
  @Column(name = "BIRTH_DATE", nullable = false)
  @Temporal(TemporalType.DATE)
  private Date dateOfBirth;

  @Audited
  @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

  @Audited
  @Column(name = "LAST_NAME", nullable = false)
  private String lastName;

  @Column(name = "MIDDLE_NAME")
  private String middleName;

  @Audited
  @Column(name = "APPLICANT_ROLE", nullable = false)
  @Enumerated
  private ApplicantRole applicantRole;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "RES_ADDRESS_ID", nullable = false)
  private Address residentialAddress;

  @ManyToOne
//  @JoinColumn(name = "APPLICATION_ID", unique=false, nullable=false, insertable=true, updatable=true)
  @JoinColumn(name = "APPLICATION_ID", nullable = false, insertable = false, updatable = false)
//  @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "PK")
  private Application application;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "CBR_SUMMARY_ID")
  private CreditBureauSummary cbrSummary;

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public ApplicantRole getApplicantRole() {
    return applicantRole;
  }

  public void setApplicantRole(ApplicantRole applicantRole) {
    this.applicantRole = applicantRole;
  }

  public Address getResidentialAddress() {
    return residentialAddress;
  }

  public void setResidentialAddress(Address residentialAddress) {
    this.residentialAddress = residentialAddress;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public CreditBureauSummary getCbrSummary() {
    return cbrSummary;
  }

  public void setCbrSummary(CreditBureauSummary cbrSummary) {
    this.cbrSummary = cbrSummary;
  }

  @Override
  public String toString() {
    return "Applicant{" +
      "ssn='" + ssn + '\'' +
      ", dateOfBirth=" + dateOfBirth +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", middleName='" + middleName + '\'' +
      ", applicantRole=" + applicantRole +
      ", residentialAddress=" + residentialAddress +
      ", application=" + application +
      ", cbrSummary=" + cbrSummary +
      '}';
  }
}
