package entities.user;

import entities.abstracts.BaseEntity;
import exceptions.UserValidationException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Objects;

public abstract class BaseUser extends BaseEntity {
    private String TIN;
    private String passwordHash;

    // Add TIN format checking

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

    public BaseUser(BaseUser other) {
        this.TIN = other.TIN;
        this.passwordHash = other.passwordHash;
    }

    // We don`t need copy-constructor? I guess no, because user with same all fields cannot exist

    public String getTIN() {
        return TIN;
    }

    public void setTIN(String TIN) throws UserValidationException {
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationException("TIN cannot be null or blank");
        }

        this.TIN = TIN;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public static class PasswordUtils {
        public static String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt(12));
        }

        public static boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
        }
    }

    // TODO:
    // public abstract Document createDocument(repository?, docdraft?, map<string, string> data?, map<string(role), Signatory>?) {
    // repository.create()? }
    // public abstract Document signDocument(repository, document) { repository.handleSign()? }
    // public abstract List<Documents> getSignedDocuments(repository) { repository.getDocuments(where user is signatory) }
    // public abstract List<Documents> getUnsignedDocuments(repository) { repository.getDocuments(where user is pending) }
    // public abstract Document declineDocument(repository, document)? {  repository.handleDecline()? }

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
