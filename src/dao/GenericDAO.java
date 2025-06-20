package dao;

import entities.abstracts.BaseEntity;

/**
 * Generic Data Access Object interface for CRUD operations on entities.
 *
 * @param <T> type of entity that extends BaseEntity
 */
public interface GenericDAO<T extends BaseEntity> {

    /**
     * Inserts a new entity into the datastore.
     *
     * @param entity the entity to insert; must not be null
     */
    void insert(T entity);

    /**
     * Reads an entity by its unique identifier.
     *
     * @param id the identifier of the entity to read; must not be null
     * @return the entity matching the given id, or null if not found
     */
    T readById(Integer id);

    /**
     * Updates an existing entity in the datastore.
     *
     * @param entity the entity to update; must not be null and must have an existing id
     */
    void update(T entity);

    /**
     * Deletes an existing entity from the datastore.
     *
     * @param entity the entity to delete; must not be null and must have an existing id
     */
    void delete(T entity);
}
