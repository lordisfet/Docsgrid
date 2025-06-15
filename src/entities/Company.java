package entities;

import entities.abstracts.BaseEntity;
import exceptions.IllegalIdException;

import java.security.cert.CertificateParsingException;
import java.util.Objects;

public class Company extends BaseEntity {
    private String companyName;

    public Company(String companyName) {
        super();

        if (companyName == null || companyName.isBlank()){
            throw new IllegalArgumentException("Company name cannot be null or blank");
        }

        this.companyName = companyName;
    }

    public Company(Integer id, String companyName) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }
        if (companyName == null || companyName.isBlank()){
            throw new IllegalArgumentException("Company name cannot be null or blank");
        }

        this.id = id;
        this.companyName = companyName;
    }

    public Company(Company other) {
        super(other);
        this.companyName = other.companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

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
