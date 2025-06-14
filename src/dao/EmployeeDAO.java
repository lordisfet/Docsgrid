package dao;

import database.DBConnection;
import entities.abstracts.BaseEntity;
import entities.user.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeDAO implements GenericDAO<Employee> {
    @Override
    public void insert(BaseEntity entity) {
        String sql = "INSERT INTO employees(tin, full_name, password, job, company_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Employee readById(Integer id) {
        return null;
    }

    @Override
    public void update(BaseEntity entity) {

    }

    @Override
    public void delete(BaseEntity entity) {

    }
}
