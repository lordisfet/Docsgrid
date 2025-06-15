package entities.user;

import entities.Company;
import exceptions.UserValidationError;

import java.util.Objects;

public class Employee extends BaseUser {
    private String fullName;
    private String jobPosition;
    private Company company;

    public Employee(String TIN, String password, String fullName, String jobPosition, Company company) throws UserValidationError {
        super(TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationError("Job position cannot be null or blank");
        }
        if (company == null) {
            throw new IllegalArgumentException("Company position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
        this.company = company;
    }

    public Employee(Integer id ,String TIN, String password, String fullName, String jobPosition, Company company) throws UserValidationError {
        super(id, TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationError("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationError("Job position cannot be null or blank");
        }
        if (company == null) {
            throw new IllegalArgumentException("Company position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
        this.company = company;
    }

    public Employee(Employee other) {
        super(other);
        this.fullName = other.fullName;
        this.jobPosition = other.jobPosition;
        this.company = other.company;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(fullName, employee.fullName) && Objects.equals(jobPosition, employee.jobPosition) && Objects.equals(company, employee.company);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fullName, jobPosition, company);
    }

    @Override
    public String toString() {
        return super.toString() + "Employee{" +
                "fullName='" + fullName + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
