package org.loandb.persistence.model;

import org.loandb.persistence.types.CreditBureauType;

import javax.persistence.*;
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
public class CreditBureauSummary extends BaseEntity {
    @Column(name = "CREDIT_SCORE", nullable = false)
    private Integer creditScore;

    @Column(name = "CREDIT_BUREAU", nullable = false)
    @Enumerated(EnumType.STRING)
    private CreditBureauType creditBureau;

    @Column(name = "REQ_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestedDate;

    @OneToOne(mappedBy = "cbrSummary")
    @XmlTransient
    private Applicant applicant;

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public CreditBureauType getCreditBureau() {
        return creditBureau;
    }

    public void setCreditBureau(CreditBureauType creditBureau) {
        this.creditBureau = creditBureau;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
