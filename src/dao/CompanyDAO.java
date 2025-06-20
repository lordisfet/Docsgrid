package dao;

import database.DBConnection;
import entities.Company;
import exceptions.IllegalIdException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for CRUD operations on Company entities and related queries.
 */
public class CompanyDAO implements GenericDAO<Company> {
    /**
     * Inserts a new Company and sets its generated ID.
     *
     * @param entity Company to insert; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void insert(Company entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Company name is required");
        }

        String sql = "INSERT INTO companies(company_name) VALUES (?) RETURNING id";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCompanyName());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Reads a Company by its ID.
     *
     * @param id ID of the Company; must be non-null and positive
     * @return Company instance or null if not found
     * @throws IllegalIdException if id is null or less than 1
     * @throws RuntimeException   on SQL errors
     */
    @Override
    public Company readById(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        String sql = "SELECT id, company_name FROM companies WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Company(rs.getInt("id"), rs.getString("company_name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Reads a Company by its name.
     *
     * @param companyName name of the Company; must not be null or blank
     * @return Company instance or null if not found
     * @throws IllegalArgumentException if companyName is null or blank
     * @throws RuntimeException         on SQL errors
     */
    public Company readByName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null or blank");
        }

        String sql = "SELECT id, company_name FROM companies WHERE company_name = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, companyName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Company(rs.getInt("id"), rs.getString("company_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Updates an existing Company.
     *
     * @param entity Company to update; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void update(Company entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Company name is required");
        }

        String sql = "UPDATE companies SET company_name = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCompanyName());
            stmt.setInt(2, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Deletes a Company.
     *
     * @param entity Company to delete; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void delete(Company entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Company name is required");
        }

        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks existence of a Company by its name.
     *
     * @param companyName name of the Company; must not be null or blank
     * @return true if a Company with the given name exists
     * @throws IllegalArgumentException if companyName is null or blank
     * @throws RuntimeException         on SQL errors
     */
    public boolean existsByName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null for exists statement");
        }

        String sql = "SELECT EXISTS (SELECT 1 FROM companies WHERE company_name = ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, companyName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
