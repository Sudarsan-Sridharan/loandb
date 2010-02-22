package org.loandb.persistence.model;

import org.hibernate.envers.Audited;
import org.loandb.persistence.types.AddressType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Set;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@Entity
@Audited
@XmlRootElement
public class Address extends BaseEntity {
    @Column(name = "ADDRESS_LINE1", nullable = false)
    private String addressLine1;
    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;
    @Column(name = "COUNTY", nullable = false)
    private String county;
    @Column(name = "ADDRESS_TYPE", nullable = false)
    @Enumerated
    private AddressType addressType;
    @Column(name = "CITY", nullable = false)
    private String city;
    @Column(name = "ZIP", nullable = false)
    private String postalCode;
    @Column(name = "STATE", nullable = false)
    private String stateCode;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlTransient
    private Set<Applicant> applicants = new HashSet<Applicant>();

    @OneToOne(mappedBy = "propertyAddress")
    @XmlTransient
    private Application application;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}

