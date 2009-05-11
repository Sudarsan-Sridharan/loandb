package org.loandb.persistence.util;

/*
 * EntityUtil - Entity dehydrator/rehydrator for JPA entities
 * Copyright (C) 2008 Steven C. Saliman
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * See www.gnu.org/licenses/licenses for more information.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

import org.hibernate.collection.PersistentSet;
import org.hibernate.proxy.HibernateProxy;
import org.loandb.persistence.dao.GenericDao;
import org.loandb.persistence.model.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * This class is a utility class for working with {@link org.loandb.persistence.model.BaseEntity} instances
 * and their descendants.  It currently deals with 2 basic areas.
 * <p/>
 * It handles object dehydration and rehydration for use in Web Service and RMI
 * calls.
 * <p/>
 * It also handles transferring transient attributes from one object to
 * another, since saving can wipe out transient attributes.
 * <p/>
 * This class is heavily dependent on the JPA provider and the types of
 * collections Entities have.  At the moment, the methods in this class only
 * work with Hibernate, and child collections must be a <code>Set</code>.  In
 * Addition, the entities must use field annotations and not method
 * annotations.
 *
 * @author Steven C. Saliman, Pinnacol Assurance
 */
public class EntityUtil {
    /**
     * logger for the class
     */
    private static final Logger LOG = LoggerFactory.getLogger(EntityUtil.class);

    /**
     * Dehydrate the given entity to prepare it for serializing for RMI, or
     * Marshalling to XML for SOAP. It is very important that this happens
     * outside a transaction, otherwise the JPA provider will assume that
     * changes made during dehydration need to be saved.  This is not a problem
     * in a Spring based application, because Spring will only create the
     * transaction when it is told, but EJB containers, such as GlassFish, will
     * create a default transaction when the service endpoint is invoked,
     * unless the
     * <code>TransactionAttribute(TransactionAttributeType.NEVER)<code>
     * annotation is present in the endpoint class.<br>
     * In unit tests, the EntityManager.clear method should be called to make
     * sure we are dealing with detached entities.
     * Dehydration basically this means 3 things:<br>
     * 1) replacing uninitialized collections with <code>null</code>
     * and replacing all initialized proxy lists with regular Lists to
     * avoid <code>LazyInitializationException</code> issues during
     * serialization.  If a collection has been initialized, then this
     * method should iterate through the child collection and dehydrate
     * each entity in the collection.  This method should also dehydrate
     * any parent entities.<br>
     * 2) Replacement of any Hibernate proxy objects with the actual entity.
     * This could mean replacing Hibernate's proxy collections with actual
     * Set or List collections, or it could mean replacing CGLib enhanced
     * classes with the original entity classes they were wrapping.
     * <br>
     * 3) Removal of circular references. This method tries to detect
     * bidirectional associations, and when found, the child's parent
     * reference is set to null to prevent XML serialization problems.
     * This method uses the persistence annotations to detect these
     * bidirectional associations, which means that if an entity contains
     * a <code>Transient</code> collection of entities that refer back to
     * the parent, the circular reference will remain.  It is up to the
     * caller to make sure we don't try to dehydrate these kinds of
     * entities.
     * <p/>
     * Note that once an entity is dehydrated, it is no longer possible to
     * access lazy-loaded collections, even if the entity is re-hydrated
     * later.
     * <p/>
     * Also note that dehydrating an entity does not save the entity's old
     * collections in any way. It is up to the caller to make a copy of the
     * object's collections before calling <code>dehydrate</code>, if access
     * to the original collections is needed.
     * <p/>
     *
     * @param entity the {@link org.loandb.persistence.model.BaseEntity} to dehydrate
     * @throws IllegalStateException if there is a problem.
     */
    public static void dehydrate(BaseEntity entity) {
        LOG.trace("dehydrate(BaseEntity)");
        // First things first.  See if we've already started this one
        if (entity == null || !entity.isHydrated()) {
            return;
        }
        // When we dehydrate children, they may cause recursive calls to
        // dehydrate.  Mark this as dehydrated so those recursive calls don't
        // attempt to do it again.
        entity.setHydrated(false);

        String msg = "Error dehydrating " + entity + ": ";
        try {
            List<Field> fields = obtainFields(entity.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = getValue(field, entity);
                if (value != null) {
                    if (field.getName().equals("hydrated")) {
                        // we're always dehydrated, this is a no-op
                        entity.setHydrated(false);
                    } else if (BaseEntity.class.isAssignableFrom(value.getClass())) {
                        // If this is another Entity, de-proxy it, dehydrate
                        // it, then set the field's value to the de-proxied
                        // value
                        value = deproxy(value, field.getType());
                        field.set(entity, value);
                        dehydrate((BaseEntity) value);
                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                        // Handle Collections, we already know it's not null,
                        // but we need to replace proxy collections with
                        // non proxy collections.
                        dehydrateCollection(entity, (Collection<?>) value, field);
                    }
                    // the implied else from above is that the value is an
                    // object that doesn't need dehydration
                }
            }
        } catch (IllegalAccessException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (InvocationTargetException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (SecurityException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (IllegalArgumentException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (NoSuchMethodException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Rehydrate the given entity so it can be saved by Hibernate.  Basically
     * this means replacing <code>null</code> collections with new
     * <code>PersistentBag</code> objects so that hibernate doesn't try to
     * cascade saves to children. If the entity has an initialized collection
     * of children, this method will attempt to detect if it is a bidirectional
     * relationship and set the parent in each child before re-hydrating each
     * child.
     * <p/>
     * This works well enough to save an entity, but not well enough to
     * use the re-hydrated entity to get previously uninitialized collections.
     *
     * @param entity the {@link BaseEntity} to rehydrate
     * @throws IllegalStateException if something goes wrong
     */
    public static void rehydrate(BaseEntity entity) {
        LOG.trace("rehydrate(BaseEntity)");
        // bail if we're already hydrated.  This avoids loops.
        if (entity == null || entity.isHydrated()) {
            return;
        }
        entity.setHydrated(true);
        String msg = "Error rehydrating " + entity + ": ";
        try {
            List<Field> fields = obtainFields(entity.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = getValue(field, entity);
                if (field.getName().equals("hydrated")) {
                    // always set to be hydrated
                    entity.setHydrated(true);
                } else if (BaseEntity.class.isAssignableFrom(field.getType())) {
                    // If this is another Entity, rehydrate it.
                    if (value != null) {
                        rehydrate((BaseEntity) value);
                    }
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    // Rehydration may result in a new collection.
                    rehydrateCollection(entity, (Collection<?>) value, field);
                }
                // The implied else block is for objects that don't need
                // rehydration.  Nothing needs to be done in that case
            }
        } catch (InvocationTargetException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (IllegalAccessException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (SecurityException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        } catch (NoSuchMethodException e) {
            msg = msg + e.getMessage();
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Determines if a collection has been initialized. This is most useful
     * in hiding Hibernate from non-DAO tasks.  An initialized collection is
     * one we can safely access, such as an initialized Hibernate PersistentBag
     * or an ordinary Set.  This method treats <code>null</code> as an
     * uninitialized set.
     *
     * @param collection the collection to check.
     * @return <code>true</code> if the collection has been initialized.
     */
    public static boolean initialized(Collection<?> collection) {
        boolean init = false;

        //TODO: expand this to support PersistentLists
        if (collection == null) {
            // null is treated as uninitialized
            init = false;
        } else if (!PersistentSet.class.isAssignableFrom(collection.getClass())) {
            // if it's not a Hibernate PersistentBag, it's initialized.
            init = true;
        } else if (!((PersistentSet) collection).wasInitialized()) {
            // If Hibernate hasn't loaded the collection, it't not initialized
            init = false;
        } else {
            // Assume Positive Intent.
            init = true;
        }
        return init;
    }

    /**
     * Copies the transient attributes from one entity to another.  This
     * is needed when we save a BaseEntity, because the
     * {@link GenericDao#save(BaseEntity)} returns a copy of the original entity,
     * refreshed from the database, which can cause the transient attributes to
     * be lost.
     *
     * @param source the source entity
     * @param dest   the destination entity.
     * @throws IllegalStateException if we can't get one of the values.
     */
    public static void copyTransientData(BaseEntity source, BaseEntity dest) {
        List<Field> fields = obtainFields(source.getClass());
        for (Field f : fields) {
            Annotation a = f.getAnnotation(Transient.class);
            if (a != null) {
                try {
                    f.setAccessible(true);
                    f.set(dest, f.get(source));
                } catch (IllegalAccessException e) {
                    String msg = null;
                    msg = "Data Object " + source + " has an inaccessable " +
                            "or mismatched transient attribute: " + f.getName();
                    LOG.warn(msg);
                    throw new IllegalStateException(msg);
                }
            }
        }
    }

    /**
     * Populate the given entity to the given depth.  A depth of 1 indicates
     * that only the entity itself needs to be populated.  A depth of 2 means
     * all the entity's children are loaded, 3 causes grandchildren to be
     * loaded, etc.
     * <p/>
     * This method can only be called within a session, or we'll get lazy
     * loading errors.
     *
     * @param entity the {@link BaseEntity} to populate
     * @param depth  the depth to populate to.  1 for just the entity, 2 for
     *               children, etc.
     */
    public static void populateToDepth(BaseEntity entity, int depth) {
        if (entity == null || depth < 2) {
            return;
        }
        // loop through fields. If a collection, populate each child.
        List<Field> fields = obtainFields(entity.getClass());
        for (Field f : fields) {
            if (Collection.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                Collection<?> collection;
                try {
                    collection = (Collection<?>) f.get(entity);
                    if (collection != null) {
                        for (Object value : collection) {
                            // the iterator causes the children to be loaded.
                            // We only need the recursive call if we want
                            // grandchildren.
                            if (depth > 2 &&
                                    BaseEntity.class.isAssignableFrom(value.getClass())) {
                                // child needs one less than parent
                                populateToDepth((BaseEntity) value, depth - 1);
                            }
                        }
                    }
                } catch (IllegalStateException e) {
                    String msg = null;
                    msg = "Data Object " + entity + " has an inaccessable " +
                            "or attribute: " + f.getName();
                    LOG.warn(msg);
                    throw new IllegalStateException(msg);
                } catch (IllegalAccessException e) {
                    String msg = null;
                    msg = "Data Object " + entity + " has an inaccessable " +
                            "or attribute: " + f.getName();
                    LOG.warn(msg);
                    throw new IllegalStateException(msg);
                }
            }
        }
    }

    /**
     * Helper method that gets all the fields of a class and it's super-classes.
     * The method stops at Entity classes, since there won't be anything
     * above that is we can dehydrate, or that would contain transient data.
     *
     * @param clazz The class whose fields we want.
     * @return a List of fields from the given class and it's parents, up to
     *         the BaseEntity class.
     */
    private static List<Field> obtainFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        if (!clazz.equals(BaseEntity.class)) {
            fields.addAll(obtainFields(clazz.getSuperclass()));
        }
        Field[] arr = clazz.getDeclaredFields();
        for (int i = 0; i < arr.length; i++) {
            fields.add(arr[i]);
        }
        return fields;
    }

    /**
     * Helper method to dehydrate collections.  If the collection is a non
     * initialized Hibernate collection, this method will replace it with
     * a null.  If it is initialized, it will replace it with a non Hibernate
     * HashSet.  This method will then dehydrate each child entity in the
     * collection after first asking the child to delete its reference to the
     * parent so that we don't have circular references.  This method is not
     * perfect, but it should take care of all of the most common data modeling
     * scenarios.
     * <p/>
     * At the moment, this assumes that we are using Lists and not Sets for
     * our child collections. We are also assuming Hibernate as a JPA provider.
     * Hibernate puts Lists in a PersistentBag object.
     *
     * @param entity     the entity containing the collection to dehydrate
     * @param collection the original collection to dehydrate
     * @param field      the field that holds this collection.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalStateException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    @SuppressWarnings("unchecked")
    private static void dehydrateCollection(BaseEntity entity,
                                            Collection<?> collection,
                                            Field field)
            throws IllegalAccessException, IllegalStateException,
            InvocationTargetException, SecurityException, IllegalArgumentException, NoSuchMethodException {
        Collection newValue = null;

        // TODO: Add support for lists.  We can do this by using the declared type
        if (PersistentSet.class.isAssignableFrom(collection.getClass())) {
            if (!((PersistentSet) collection).wasInitialized()) {
                // non-initialized, so dehydrate with a null.
                newValue = null;
            } else {
                // replace PersistentBag with ArrayList
                newValue = new HashSet();
                newValue.addAll(collection);
            }
        } else {
            // Just use the existing collection.
            newValue = collection;
        }
        if (newValue != null) {
            // Dehydrate the children.
            Field childsParent = null;
            boolean looked = false;
            for (Object child : collection) {
                // dehydrate each child of it is a BaseEntity.
                if (BaseEntity.class.isAssignableFrom(child.getClass())) {
                    // See if the child pointed to the parent.
                    // we only need to do this once...
                    if (childsParent == null && !looked) {
                        looked = true;
                        childsParent = obtainChildsParentField(entity, field,
                                child.getClass());
                    }
                    // set the child's parent to null
                    if (childsParent != null) {
                        childsParent.set(child, null);
                    }
                    dehydrate((BaseEntity) child);
                }
            }
        }
        setValue(field, entity, newValue);
    }

    /**
     * Helper method to rehydrate collections. It replaces nulls with new
     * proxy collections so Hibernate doesn't try to disassociate children,
     * which Hibernate will try to do if it sees that the parent went from
     * having a child collection to not having a child collection. Hibernate
     * will also try to disassociate children if it sees that the collection is
     * not a Hibernate proxy object such as PersistentBag or PersistentSet.
     * <p/>
     * If the collection is not null, this method will detect bidirectional
     * associations and re-inject the parent into each child after re-hydrating
     * the child.  It is done after so that we don't try to rehydrate this
     * object twice - it is entirely possible that we hit a child collection
     * before we hit the hydrate field.
     * <p/>
     * This method assumes Hibernate as a provider, and that Lists are used
     * for child collections and not Sets
     *
     * @param entity     the entity containing the collection to dehydrate
     * @param collection the child collection to rehydrate
     * @param field      the field that contains the collection.
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalStateException
     */
    private static void rehydrateCollection(BaseEntity entity,
                                            Collection<?> collection,
                                            Field field)
            throws SecurityException, NoSuchMethodException,
            IllegalStateException, IllegalAccessException,
            InvocationTargetException {
        // GlassFish does some strange things when a null comes in for a
        // collection attribute.  It makes a collection that contains a null.
        // Try to detect this and set the collection to null.
        if (collection != null) {
            boolean hasNull = false;
            for (Object child : collection) {
                if (child == null) {
                    hasNull = true;
                }
            }
            if (hasNull) {
                collection = null;
            }
        }

        // TODO: change this method so it can handle sets as well as lists
        // this can be done by using the original field definition and choosing
        // the correct object accordingly.
        if (collection == null) {
            // We only want to put in an empty PersistentBag if:
            // 1. The parent is persistent(it has an id) This is safe because
            //    IDs don't change during the rehydration process.
            // 2. The collection is persistent (not Transient).
            Annotation a = field.getAnnotation(Transient.class);
            if ((a == null) && (entity.getId() != null)) {
                setValue(field, entity, new PersistentSet());
            }
        } else {
            // Note that in this case, we'll have a collection that isn't
            // a Hibernate PersistentBag.  This is OK.
            Field childsParent = null;
            boolean looked = false;
            for (Object child : collection) {
                if (BaseEntity.class.isAssignableFrom(child.getClass())) {
                    rehydrate((BaseEntity) child);
                }
                // remember this needs to come last.
                // we only need to do this once...
                if (childsParent == null && !looked) {
                    looked = true;
                    childsParent = obtainChildsParentField(entity, field,
                            child.getClass());
                }
                if (childsParent != null) {
                    childsParent.set(child, entity);
                }
            }
        }
    }

    /**
     * Helper to the helper that gets the child's parent field.
     *
     * @param entity     the entity containing the child
     * @param field      the field containing the child
     * @param childClazz the class of the child
     */
    private static Field obtainChildsParentField(BaseEntity entity,
                                                 Field field,
                                                 Class<?> childClazz) {
        Field childsParent = null;
        String mappedBy = null;
        String msg = null;
        // See if the current method has a OneToMany or OneToOne
        // annotation with a "mappedBy" with indicates a
        // Bidirectional association.
        Annotation a = field.getAnnotation(OneToMany.class);
        if (a != null) {
            mappedBy = ((OneToMany) a).mappedBy();
        } else {
            a = field.getAnnotation(OneToOne.class);
            if (a != null) {
                mappedBy = ((OneToOne) a).mappedBy();
            }
        }
        // if we got an annotation with a "mappedBy", process it
        if ((mappedBy != null) && (mappedBy.length() > 0)) {
            // convert case
            try {
                Class<?> currClazz = childClazz;
                // We can't use getField for a private field...
                while (childsParent == null && !currClazz.equals(Object.class)) {
                    Field[] fields = currClazz.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].getName().equals(mappedBy)) {
                            childsParent = fields[i];
                            childsParent.setAccessible(true);
                            break;
                        }
                    }
                }
                if (childsParent == null) {
                    msg = "Data Object " + entity + " has a child collecion " +
                            "marked as bidrectional, but the child's parent " +
                            "attribute (" + mappedBy + ") can't be found";
                    LOG.warn(msg);
                    throw new NullPointerException(msg);
                }
                if (!entity.getClass().isAssignableFrom(childsParent.getType())) {
                    msg = "Data Object " + entity + " has a child collecion " +
                            "marked as bidrectional, but the child's parent " +
                            "attribute (" + mappedBy + ") is the wrong type";
                    LOG.warn(msg);
                    throw new NullPointerException(msg);
                }
            } catch (SecurityException e) {
                LOG.warn("Data Object " + entity + " has a child collecion " +
                        "marked as bidrectional, but the child doesn't " +
                        "have a public field for the " + mappedBy +
                        " attribute");
            }
        }
        return childsParent;
    }

    /**
     * Gets the value from the given field.  We can't just use field.get
     * because Hibernate doesn't always store the value in the field. It seems
     * to store it in some CGLIB field, using proxy methods to get to it. We,
     * therefore, need to use those same proxies, or we won't be getting the
     * correct value
     *
     * @param field  the field object representing the field with the value
     *               we want.
     * @param entity the entity with the value we want
     * @return the value for the given field in the given entity.
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static Object getValue(Field field, BaseEntity entity)
            throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // we need to call the method to get the proxy...
        String name = field.getName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        Class<?> type = field.getType();
        if (boolean.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type)) {
            name = "is" + name;
        } else {
            name = "get" + name;
        }
        Object value = null;
        // Get the value.  We need to try using the "get" method first, but
        // not all fields will have a method (SERIAL_VERSION_UID for example),
        // so we need to get the actual field value if the method doesn't
        // exist.
        try {
            Method method = field.getDeclaringClass().getMethod(name);
            value = method.invoke(entity);
        } catch (NoSuchMethodException e) {
            // not all fields will have a getter, so just get it from the
            // field directly
            value = field.get(entity);
        }
        return value;
    }

    /**
     * Sets the value of the given field in the given entity.
     *
     * @param field  the field we want to set.
     * @param entity the entity we want to change
     * @param value  the value to set.
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @see #getValue(Field, BaseEntity)
     */
    private static void setValue(Field field, BaseEntity entity, Object value)
            throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String name = field.getName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        name = "set" + name;
        // Try to set the value using the "set" method first.
        try {
            Method method = field.getDeclaringClass().getMethod(name, field.getType());
            method.invoke(entity, new Object[]{value});
        } catch (NoSuchMethodException e) {
            field.set(entity, value);
        }
    }

    /**
     * Helper method to replace proxy objects with actual classes. This is
     * needed because Flex won't know how to map a proxy class to a Flex
     * entity, and even if it did, there would be a lot of extra data that
     * Flex doesn't need.
     *
     * @param <T>         The class that we are casting to.
     * @param maybeProxy  The object to de-proxy
     * @param entityClass The class to cast to
     * @return the original object, cast to the entity class.
     * @throws ClassCastException If we can't make the cast.
     */
    private static <T> T deproxy(Object maybeProxy, Class<T> entityClass) throws ClassCastException {
        if (maybeProxy instanceof HibernateProxy) {
            return entityClass.cast(((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation());
        }
        return entityClass.cast(maybeProxy);
    }
}
