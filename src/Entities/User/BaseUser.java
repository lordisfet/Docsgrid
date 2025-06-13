package Entities.User;

import Exceptions.UserValidationError;

public abstract class BaseUser implements IEntity {
    private final int id;
    private final TypeOfUser type;
    private String TIN;

    public BaseUser(int id, TypeOfUser type, String TIN) throws UserValidationError {
        // no constraints for id?
        if (type == null) {
            throw new UserValidationError("Type cannot be null");
        }
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationError("TIN cannot be null or blank");
        }

        this.id = id;
        this.type = type;
        this.TIN = TIN;
    }

    @Override
    public int getId() {
        return id;
    }

    public TypeOfUser getType() {
        return type;
    }

    public String getTIN() {
        return TIN;
    }

    public void setTIN(String TIN) throws UserValidationError {
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationError("TIN cannot be null or blank");
        }

        this.TIN = TIN;
    }

    // TODO:
    // public abstract Document createDocument(repository?, docdraft?, map<string, string> data?, map<string(role), Signatory>?) {
    // repository.create()? }
    // public abstract Document signDocument(repository, document) { repository.handleSign()? }
    // public abstract List<Documents> getSignedDocuments(repository) { repository.getDocuments(where user is signatory) }
    // public abstract List<Documents> getUnsignedDocuments(repository) { repository.getDocuments(where user is pending) }
    // public abstract Document declineDocument(repository, document)? {  repository.handleDecline()? }

    @Override
    public String toString() {
        return "BaseUser{" +
                "id=" + id +
                ", type=" + type +
                ", TIN='" + TIN + '\'' +
                '}';
    }
}
