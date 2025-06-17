package entities.abstracts;

import exceptions.IllegalIdException;

import java.util.Objects;

public abstract class BaseEntity {
    protected Integer id;

    public BaseEntity() {}

    public BaseEntity(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        this.id = id;
    }

    public BaseEntity(BaseEntity other) {
        if (other == null) {
            throw new IllegalArgumentException("BaseEntity cannot be null");
        }

        this.id = other.id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                '}';
    }
}
