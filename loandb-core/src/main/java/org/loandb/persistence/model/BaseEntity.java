package org.loandb.persistence.model;

import com.saliman.entitypruner.PrunableEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Map;

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
    private boolean pruned = false;

    @Transient
    @XmlTransient
    private Map<String, String> fieldIdMap;

    public Long getId() {
        return id;
    }

    public boolean isPruned() {
        return pruned;
    }

    public void setPruned(boolean pruned) {
        this.pruned = pruned;
    }

    public Map<String, String> getFieldIdMap() {
        return fieldIdMap;
    }

    public void setFieldIdMap(Map<String, String> fieldIdMap) {
        this.fieldIdMap = fieldIdMap;
    }

    public boolean isPersistent() {
        return id != null;
    }
}
