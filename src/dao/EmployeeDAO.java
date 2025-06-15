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
}
