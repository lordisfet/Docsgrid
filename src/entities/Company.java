package entities;

import entities.abstracts.BaseEntity;
import exceptions.CompanyValidationException;
import exceptions.IllegalIdException;

import java.util.Objects;

/**
 * Entity representing a company with a unique name.
 */
public class Company extends BaseEntity {
    private String companyName;

    /**
     * Constructs a new Company with the specified name.
     *
     * @param companyName the name of the company; must not be null or blank
     * @throws CompanyValidationException if companyName is null or blank
     */
    public Company(String companyName) {
        if (companyName == null || companyName.isBlank()){
            throw new CompanyValidationException("Company name cannot be null or blank");
        }

        this.companyName = companyName;
    }

    /**
     * Constructs a Company with an existing ID and name.
     *
     * @param id          the ID of the company; passed to BaseEntity
     * @param companyName the name of the company; must not be null or blank
     * @throws CompanyValidationException if companyName is null or blank
     * @throws IllegalIdException if id is invalid
     */
    public Company(Integer id, String companyName) {
        super(id);

        if (companyName == null || companyName.isBlank()){
            throw new CompanyValidationException("Company name cannot be null or blank");
        }

        this.companyName = companyName;
    }

    /**
     * Copy constructor.
     *
     * @param other the Company to copy; must not be null
     */
    public Company(Company other) {
        super(other);
        this.companyName = other.companyName;
    }

    /**
     * Returns the company name.
     *
     * @return the name of the company
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name.
     *
     * @param companyName the new company name; should not be null or blank
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(companyName, company.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(companyName);
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyName='" + companyName + '\'' +
                '}';
    }
}