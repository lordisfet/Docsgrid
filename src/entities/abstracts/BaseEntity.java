package entities.abstracts;

import exceptions.IllegalIdException;

public abstract class BaseEntity {
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        this.id = id;
    }
}
