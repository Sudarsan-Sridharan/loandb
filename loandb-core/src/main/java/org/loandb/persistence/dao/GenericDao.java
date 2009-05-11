package org.loandb.persistence.dao;

import java.io.Serializable;
import java.util.List;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public interface GenericDao<T, PK extends Serializable> {
    List<T> getAll();

    T get(PK id);

    boolean exists(PK id);

    T save(T entity);

    T update(T entity);

    void remove(PK id);
}
