package org.loandb.persistence.model;

import org.hibernate.envers.Audited;
import org.loandb.persistence.types.LoanType;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @Audited
    @Column(name = "LOAN_AMT", nullable = false)
    private Double loanAmount;

    @Audited
    @Column(name = "LOAN_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROP_ADDRESS_ID", nullable = false)
    private Address propertyAddress;

    //    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    @XmlTransient
    private Set<Applicant> applicant = new HashSet<Applicant>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DECISION_ID")
    private Decision decision;

    /**
     * Applications should never get deleted from the Loan system. If the loan product is no longer active,
     * they should be archived for future reference.
     */
    @AssertTrue(groups = DeletionAttributes.class)
    private boolean archived;

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

    @XmlElement
    @XmlElementWrapper(name = "applicants")
    public Set<Applicant> getApplicant() {
        return applicant;
    }

    public void setApplicant(Set<Applicant> applicants) {
        this.applicant = applicants;
    }

    public Applicant addApplicant(Applicant applicant) {
        getApplicant().add(applicant);
        applicant.setApplication(this);
        return applicant;
    }

    public Applicant removeApplicant(Applicant applicant) {
        getApplicant().remove(applicant);
        applicant.setApplication(null);
        return applicant;
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

}
