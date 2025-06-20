package entities.user;

import entities.abstracts.BaseEntity;
import exceptions.UserValidationException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Objects;

/**
 * Abstract base class for user entities, handling TIN and password hashing.
 */
public abstract class BaseUser extends BaseEntity {
    private String TIN;
    private String passwordHash;

    /**
     * Constructs a new BaseUser with TIN and raw password.
     * Password is hashed using BCrypt.
     *
     * @param TIN      tax identification number; must not be null or blank
     * @param password raw password; must not be null or blank
     * @throws UserValidationException if validation fails
     */
    public BaseUser(String TIN, String password) throws UserValidationException {
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationException("TIN cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new UserValidationException("passwordHash cannot be null or blank");
        }

        this.TIN = TIN;
        this.passwordHash = PasswordUtils.hashPassword(password);
    }

    /**
     * Constructs a BaseUser with existing ID, TIN, and raw password.
     * Password is hashed using BCrypt.
     *
     * @param id       entity ID; must not be null or negative
     * @param TIN      tax identification number; must not be null or blank
     * @param password raw password; must not be null or blank
     * @throws UserValidationException if validation fails
     */
    public BaseUser(Integer id, String TIN, String password) throws UserValidationException {
        super(id);

        if (id == null || id < 0) {
            throw new UserValidationException("id cannot be null or blank");
        }
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationException("TIN cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new UserValidationException("passwordHash cannot be null or blank");
        }

        this.TIN = TIN;
        this.passwordHash = PasswordUtils.hashPassword(password);
    }

    /**
     * Copy constructor.
     *
     * @param other BaseUser to copy; must not be null
     */
    public BaseUser(BaseUser other) {
        super(other);

        this.TIN = other.TIN;
        this.passwordHash = other.passwordHash;
    }

    /**
     * Returns the TIN.
     *
     * @return tax identification number
     */
    public String getTIN() {
        return TIN;
    }

    /**
     * Sets the TIN after validation.
     *
     * @param TIN tax identification number; must not be null or blank
     * @throws UserValidationException if TIN is null or blank
     */
    public void setTIN(String TIN) throws UserValidationException {
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationException("TIN cannot be null or blank");
        }

        this.TIN = TIN;
    }

    /**
     * Returns the stored password hash.
     *
     * @return BCrypt-hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash directly.
     *
     * @param passwordHash BCrypt-hashed password; should be valid
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Utility class for password hashing and verification.
     */
    public static class PasswordUtils {
        /**
         * Hashes a raw password using BCrypt.
         *
         * @param password raw password
         * @return hashed password string
         */
        public static String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt(12));
        }

        /**
         * Verifies a raw password against its hash.
         *
         * @param password       raw password
         * @param hashedPassword stored BCrypt hash
         * @return true if match, false otherwise
         */
        public static boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseUser baseUser = (BaseUser) o;
        return  Objects.equals(TIN, baseUser.TIN) && Objects.equals(passwordHash, baseUser.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, TIN, passwordHash);
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "TIN='" + TIN + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}
