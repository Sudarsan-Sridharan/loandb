package org.loandb.persistence.model;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Email;
import org.loandb.persistence.types.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
    @Column(name = "SSN", length =11, nullable = false)
    @Pattern(regexp = "[0-9][0-9][0-9][-][0-9][0-9][-][0-9][0-9][0-9][0-9]")
    private String ssn;

    @Audited
    @Column(name = "BIRTH_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @Past
    private Date dateOfBirth;

    @Audited
    @Column(name = "FIRST_NAME", length = 32, nullable = false)
    private String firstName;

    @Audited
    @Column(name = "LAST_NAME", length = 32, nullable = false)
    private String lastName;

    @Column(name = "MIDDLE_NAME", length = 32)
    private String middleName;

    @Column(name = "SUFFIX", length = 6)
    @Enumerated(value = EnumType.STRING)
    private SuffixType suffix;

    @Column(name = "TITLE", length = 3)
    @Enumerated(value = EnumType.STRING)
    private Title title;

    @Column(name = "EMAIL_ADDR", length = 255, nullable = false)
    @Email
    private String emailAddress;

    @Column(name = "ID_NUMBER", length = 255, nullable = false)
    private String identificationNumber;

    @Column(name = "ID_TYPE", length = 8, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private IdentificationType identificationType;

    @Column(name = "ID_ISSUE_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @Past
    private Date identificationIssueDate;

    @Column(name = "ID_EXPIRATION_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @Future
    private Date identificationExpirationDate;

    @Column(name = "ISSUING_AUTHORITY", nullable = false)
    private String identificationAuthority;

    @Column(name = "PHONE_NUM", nullable = false)
    private String phoneNumber;

    @Column(name = "PHONE_TYPE", length = 4, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PhoneType phoneType;

    @Column(name = "GENDER_TYPE", length = 6, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GenderType genderType;

    @Audited
    @Column(name = "APPLICANT_ROLE", length = 16, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApplicantRole applicantRole;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RES_ADDRESS_ID", nullable = false)
    private Address residentialAddress;

    @ManyToOne
//  @JoinColumn(name = "APPLICATION_ID", unique=false, nullable=false, insertable=true, updatable=true)
    @JoinColumn(name = "APPLICATION_ID", nullable = false, insertable = false, updatable = false)
//  @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "PK")
    @XmlTransient
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

    public SuffixType getSuffix() {
        return suffix;
    }

    public void setSuffix(SuffixType suffix) {
        this.suffix = suffix;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public Date getIdentificationIssueDate() {
        return identificationIssueDate;
    }

    public void setIdentificationIssueDate(Date identificationIssueDate) {
        this.identificationIssueDate = identificationIssueDate;
    }

    public Date getIdentificationExpirationDate() {
        return identificationExpirationDate;
    }

    public void setIdentificationExpirationDate(Date identificationExpirationDate) {
        this.identificationExpirationDate = identificationExpirationDate;
    }

    public String getIdentificationAuthority() {
        return identificationAuthority;
    }

    public void setIdentificationAuthority(String identificationAuthority) {
        this.identificationAuthority = identificationAuthority;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

}
