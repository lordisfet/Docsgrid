package dao;

import entities.abstracts.BaseEntity;

public interface GenericDAO<T extends BaseEntity> {
    void insert(T entity);

    T readById(Integer id);

    void update(T entity);

    void delete(T entity);
}
