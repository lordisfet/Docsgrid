package dao;

import database.DBConnection;
import entities.Company;
import exceptions.IllegalIdException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDAO implements GenericDAO<Company> {
    @Override
    public void insert(Company entity) {
        if (entity.getCompanyName() == null || entity.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name is required.");
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

    @Override
    public void update(Company entity) {

    }

    @Override
    public void delete(Company entity) {

    }
}
