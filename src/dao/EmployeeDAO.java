package dao;

import database.DBConnection;
import entities.Company;
import entities.abstracts.BaseEntity;
import entities.user.BaseUser;
import entities.user.Employee;
import exceptions.IllegalIdException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAO implements GenericDAO<Employee> {
    @Override
    public void insert(Employee entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Employee entity cannot be null.");
        }
        if (entity.getTIN() == null || entity.getTIN().isBlank()) {
            throw new IllegalArgumentException("TIN is required.");
        }
        if (entity.getFullName() == null || entity.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (entity.getPasswordHash() == null || entity.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Password hash is required.");
        }
        if (entity.getJobPosition() == null || entity.getJobPosition().isBlank()) {
            throw new IllegalArgumentException("Job position is required.");
        }
        if (entity.getCompany() == null || entity.getCompany().getId() == null) {
            throw new IllegalArgumentException("Company with valid ID is required.");
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

    @Override
    public Employee readById(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException();
        }

        String sql = "SELECT id, tin, fullname, password_hash, job, company_id FROM employees WHERE id = ?";
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
                    rs.getString("full_name"),
                    rs.getString("password_hash"),
                    rs.getString("job"),
                    new Company(companyDAO.readById(rs.getInt("id"))));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Employee entity) {

    }

    @Override
    public void delete(Employee entity) {

    }
}
