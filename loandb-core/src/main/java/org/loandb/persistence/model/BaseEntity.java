package org.loandb.persistence.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseEntity implements PrunableEntity, Serializable {
    @Id
    @GeneratedValue
    @Column(name = "PK")
    @XmlTransient
    private Long id;

    @Transient
    @XmlTransient
    private boolean hydrated;

    @Transient
    @XmlTransient
    private boolean pruned;

    public Long getId() {
        return id;
    }

    public boolean isHydrated() {
        return hydrated;
    }

    public void setHydrated(boolean hydrated) {
        this.hydrated = hydrated;
    }

    public boolean isPruned() {
        return pruned;
    }

    public void setPruned(boolean pruned) {
        this.pruned = pruned;
    }
}
