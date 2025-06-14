package dao;

import entities.abstracts.BaseEntity;

public interface GenericDAO<T extends BaseEntity> {
    void insert(BaseEntity entity);

    T readById(Integer id);

    void update(BaseEntity entity);

    void delete(BaseEntity entity);
}
