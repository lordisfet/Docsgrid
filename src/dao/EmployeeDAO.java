package dao;

import database.DBConnection;
import entities.Company;

import entities.user.Employee;
import exceptions.IllegalIdException;
import exceptions.UserValidationException;
import org.postgresql.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static entities.user.BaseUser.PasswordUtils.hashPassword;

/**
 * DAO for CRUD operations on Employee entities.
 */
public class EmployeeDAO implements GenericDAO<Employee> {
    /**
     * Inserts a new employee and sets its generated ID.
     *
     * @param entity Employee to insert; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void insert(Employee entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Employee entity cannot be null");
        }

        String sql = "INSERT INTO employees(tin, full_name, password_hash, job, company_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTIN());
            stmt.setString(2, entity.getFullName());
            stmt.setString(3, entity.getPasswordHash());
            stmt.setString(4, entity.getJobPosition());
            stmt.setInt(5, entity.getCompany().getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entity.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an employee by its ID.
     *
     * @param id ID of the employee; must be non-null and positive
     * @return Employee instance or null if not found
     * @throws IllegalIdException if id is null or less than 1
     * @throws RuntimeException   on SQL errors
     */
    @Override
    public Employee readById(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        String sql = "SELECT id, tin, full_name, password_hash, job, company_id FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            CompanyDAO companyDAO = new CompanyDAO();
            if (rs.next()) {

            }
            return new Employee(
                    rs.getInt("id"),
                    rs.getString("tin"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("job"),
                    new Company(companyDAO.readById(rs.getInt("company_id"))));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an employee by its TIN.
     *
     * @param TIN TIN, must be in NNN-NN-NNNN format.
     * @return Employee instance or null if not found
     * @throws UserValidationException Incorrect TIN
     * @throws RuntimeException   on SQL errors
     */
    public Employee readByTIN(String TIN) {
        if (TIN == null || TIN.isBlank()) {
            throw new UserValidationException("TIN cannot be null or blank");
        }

        String sql = "SELECT id, tin, full_name, password_hash, job, company_id FROM employees WHERE tin = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, TIN);

            ResultSet rs = stmt.executeQuery();
            CompanyDAO companyDAO = new CompanyDAO();
            if (rs.next()) {}
            return new Employee(
                    rs.getInt("id"),
                    rs.getString("tin"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("job"),
                    new Company(companyDAO.readById(rs.getInt("company_id"))));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing employee.
     *
     * @param entity Employee to update; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void update(Employee entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Employee entity cannot be null");
        }

        String sql = "UPDATE employees SET tin = ?, full_name = ?, password_hash = ?, job = ?, company_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getTIN());
            stmt.setString(2, entity.getFullName());
            stmt.setString(3, entity.getPasswordHash());
            stmt.setString(4, entity.getJobPosition());
            stmt.setInt(5, entity.getCompany().getId());
            stmt.setInt(6, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an employee entity.
     *
     * @param entity Employee to delete; must not be null
     * @throws IllegalArgumentException if entity is null
     * @throws RuntimeException         on SQL errors
     */
    @Override
    public void delete(Employee entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Employee entity cannot be null");
        }

        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks existence of an employee by TIN.
     *
     * @param tin Tax Identification Number; must not be null or blank
     * @return true if an employee with the given TIN exists
     * @throws IllegalArgumentException if tin is null or blank
     * @throws RuntimeException         on SQL errors
     */
    public boolean existsByTIN(String tin) {
        if (tin == null || tin.isBlank()) {
            throw new IllegalArgumentException("TIN cannot be null or empty for exists statement");
        }

        String sql = "SELECT EXISTS(SELECT 1 FROM employees WHERE tin = ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tin);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Reads an employee by TIN and password hash.
     *
     * @param tin      Tax Identification Number; must not be null or blank
     * @param password Password hash; must not be null or blank
     * @return Employee instance or null if not found
     * @throws IllegalArgumentException if password is null or blank
     * @throws RuntimeException         on SQL errors
     */
    public Employee readByTIN(String tin) {
        if (tin == null || tin.isBlank()) {
            throw new IllegalArgumentException("TIN cannot be null or empty");

        }

        String sql = "SELECT id, tin, full_name, password_hash, job, company_id FROM employees WHERE tin = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tin);

            ResultSet rs = stmt.executeQuery();
            CompanyDAO companyDAO = new CompanyDAO();
            if (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("tin"),
                        "temp",
                        rs.getString("full_name"),
                        rs.getString("job"),
                        new Company(companyDAO.readById(rs.getInt("company_id"))));
                employee.setPasswordHash(rs.getString("password_hash"));

                return employee;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
