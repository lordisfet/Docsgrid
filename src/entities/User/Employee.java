package entities.User;

import exceptions.UserValidationError;

public class Employee extends BaseUser {
    private String fullName;
    // TODO: private Company company;
    private String jobPosition;

    public Employee(String TIN, String password, String fullName, String jobPosition) throws UserValidationError {
        super(TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationError("Job position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
    }

    public Employee(Integer id ,String TIN, String password, String fullName, String jobPosition) throws UserValidationError {
        super(id, TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationError("Job position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) throws UserValidationError {
        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }

        this.fullName = fullName;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) throws UserValidationError {
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }

        this.jobPosition = jobPosition;
    }

    @Override
    public String toString() {
        return super.toString() + "Employee{" +
                "fullName='" + fullName + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                '}';
    }
}
