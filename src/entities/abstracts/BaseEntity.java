package entities.abstracts;

import exceptions.IllegalIdException;

import java.util.Objects;

/**
 * Abstract base class for entities with an integer identifier.
 */
public abstract class BaseEntity {
    /**
     * Unique identifier for the entity; must be positive.
     */
    protected Integer id;

    /**
     * Default constructor.
     * Initializes a BaseEntity without setting its ID.
     */
    public BaseEntity() {}

    /**
     * Constructs a BaseEntity with the specified ID.
     *
     * @param id the identifier; must be non-null and greater than zero
     * @throws IllegalIdException if id is null or less than 1
     */
    public BaseEntity(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        this.id = id;
    }

    /**
     * Copy constructor.
     *
     * @param other the BaseEntity to copy; must not be null
     * @throws IllegalArgumentException if other is null
     */
    public BaseEntity(BaseEntity other) {
        if (other == null) {
            throw new IllegalArgumentException("BaseEntity cannot be null");
        }

        this.id = other.id;
    }

    /**
     * Returns the entity ID.
     *
     * @return the identifier (may be null if not set)
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the entity ID.
     *
     * @param id the new identifier; must be non-null and greater than zero
     * @throws IllegalIdException if id is null or less than 1
     */
    public void setId(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        this.id = id;
    }

    /**
     * Compares this entity to another object for equality based on ID.
     *
     * @param o the object to compare with
     * @return true if both are of the same class and have equal IDs
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns a hash code value for the entity based on ID.
     *
     * @return hash code of the ID (or 0 if ID is null)
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Returns a string representation of the entity.
     *
     * @return string in the format BaseEntity{id=<id>}.
     */
    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                '}';
    }
}