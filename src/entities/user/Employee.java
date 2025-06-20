package entities.user;

import entities.Company;
import exceptions.UserValidationException;

import java.util.Objects;

/**
 * Represents an employee with personal and job-related information.
 */
public class Employee extends BaseUser {
    private String fullName;
    private String jobPosition;
    private Company company;

    /**
     * Constructs a new Employee with the specified details.
     *
     * @param TIN         tax identification number; must not be null or blank
     * @param password    raw password; must not be null or blank
     * @param fullName    employee's full name; must not be null or blank
     * @param jobPosition job position; must not be null or blank
     * @param company     associated Company; must not be null
     * @throws UserValidationException if any argument is invalid
     */
    public Employee(String TIN, String password, String fullName, String jobPosition, Company company) throws UserValidationException {
        super(TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationException("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationException("Job position cannot be null or blank");
        }
        if (company == null) {
            throw new UserValidationException("Company position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
        this.company = company;
    }

    /**
     * Constructs an Employee with an existing ID and details.
     *
     * @param id          employee ID; passed to BaseUser for validation
     * @param TIN         tax identification number; must not be null or blank
     * @param password    raw password; must not be null or blank
     * @param fullName    employee's full name; must not be null or blank
     * @param jobPosition job position; must not be null or blank
     * @param company     associated Company; must not be null
     * @throws UserValidationException if any argument is invalid
     */
    public Employee(Integer id, String TIN, String password, String fullName, String jobPosition, Company company) throws UserValidationException {
        super(id, TIN, password);

        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationException("Full name cannot be null or blank");
        }
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationException("Job position cannot be null or blank");
        }
        if (company == null) {
            throw new UserValidationException("Company position cannot be null or blank");
        }

        this.fullName = fullName;
        this.jobPosition = jobPosition;
        this.company = company;
    }

    /**
     * Copy constructor for Employee.
     *
     * @param other Employee to copy; must not be null
     * @throws UserValidationException if other is null
     */
    public Employee(Employee other) throws UserValidationException {
        super(other);

        if (other == null) {
            throw new UserValidationException("Employee for copy cannot be null");
        }

        this.fullName = other.fullName;
        this.jobPosition = other.jobPosition;
        this.company = other.company;
    }

    /**
     * Returns the full name of the employee.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name after validation.
     *
     * @param fullName new full name; must not be null or blank
     * @throws UserValidationException if fullName is invalid
     */
    public void setFullName(String fullName) throws UserValidationException {
        if (fullName == null || fullName.isBlank()) {
            throw new UserValidationException("Full name cannot be null or blank");
        }

        this.fullName = fullName;
    }

    /**
     * Returns the job position of the employee.
     *
     * @return job position
     */
    public String getJobPosition() {
        return jobPosition;
    }

    /**
     * Sets the job position after validation.
     *
     * @param jobPosition new job position; must not be null or blank
     * @throws UserValidationException if jobPosition is invalid
     */
    public void setJobPosition(String jobPosition) throws UserValidationException {
        if (jobPosition == null || jobPosition.isBlank()) {
            throw new UserValidationException("Job position cannot be null or blank");
        }

        this.jobPosition = jobPosition;
    }

    /**
     * Returns the associated company.
     *
     * @return Company instance
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Sets the associated company.
     *
     * @param company new Company; may be null if to be set later
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(fullName, employee.fullName)
                && Objects.equals(jobPosition, employee.jobPosition)
                && Objects.equals(company, employee.company);
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
                ", company=" + company +
                '}';
    }
}