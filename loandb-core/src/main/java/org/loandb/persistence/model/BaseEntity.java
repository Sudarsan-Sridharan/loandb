package org.loandb.persistence.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
@MappedSuperclass
public abstract class BaseEntity implements PrunableEntity, Serializable {
  @Id
  @GeneratedValue
  @Column(name = "PK")
  private Long id;

  @Transient
  private boolean hydrated;

  @Transient
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
