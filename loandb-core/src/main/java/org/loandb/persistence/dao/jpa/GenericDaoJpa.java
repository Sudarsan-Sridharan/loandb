package org.loandb.persistence.dao.jpa;

import org.loandb.persistence.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public abstract class GenericDaoJpa<T, PK extends Serializable> implements GenericDao<T, PK> {

    @PersistenceContext
    protected EntityManager entityManager;

    private Class<T> persistentClass;

    public GenericDaoJpa(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public List<T> getAll() {
        return this.entityManager.createQuery(
                "select entity from " + this.persistentClass.getName() + " entity")
                .getResultList();
    }

    public T get(PK id) {
        T entity = this.entityManager.find(this.persistentClass, id);
        return entity;
    }

    public boolean exists(PK id) {
        T entity = this.entityManager.find(this.persistentClass, id);
        return entity != null;
    }

    public T save(T entity) {
        this.entityManager.persist(entity);
        return entity;
    }

    public T update(T entity) {
        return this.entityManager.merge(entity);
    }


    public void remove(PK id) {
        this.entityManager.remove(this.get(id));
    }
}