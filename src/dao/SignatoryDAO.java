package dao;

import database.DBConnection;
import entities.Signatory;
import entities.user.Employee;
import exceptions.SignatoryValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SignatoryDAO {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public void insert(int documentId, Signatory signatory) {
        if (signatory == null) {
            throw new IllegalArgumentException("Signatory cannot be null");
        }
        String sql = "INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, signatory.getEmployee().getId());
            stmt.setBoolean(3, signatory.isSigned());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Signatory", e);
        }
    }

    public Signatory read(int documentId, int employeeId) {
        String sql = "SELECT sign_status FROM signatories WHERE document_id = ? AND employee_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            stmt.setInt(2, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                boolean status = rs.getBoolean("sign_status");
                Employee emp = employeeDAO.readById(employeeId);
                return new Signatory(emp, status);
            }
        } catch (SQLException | SignatoryValidationException e) {
            throw new RuntimeException("Error reading Signatory", e);
        }
    }

    public List<Signatory> findByDocumentId(int documentId) {
        String sql = "SELECT employee_id, sign_status FROM signatories WHERE document_id = ?";
        List<Signatory> list = new ArrayList<>();
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int empId = rs.getInt("employee_id");
                    boolean status = rs.getBoolean("sign_status");
                    Employee emp = employeeDAO.readById(empId);
                    list.add(new Signatory(emp, status));
                }
            }
        } catch (SQLException | SignatoryValidationException e) {
            throw new RuntimeException("Error fetching Signatories", e);
        }
        return list;
    }

    public void update(int documentId, Signatory signatory) {
        String sql = "UPDATE signatories SET sign_status = ? WHERE document_id = ? AND employee_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, signatory.isSigned());
            stmt.setInt(2, documentId);
            stmt.setInt(3, signatory.getEmployee().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Signatory", e);
        }
    }

    public void deleteByDocumentId(int documentId) {
        String sql = "DELETE FROM signatories WHERE document_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, documentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Signatories by document", e);
        }
    }

    public void delete(int documentId, int employeeId) {
        String sql = "DELETE FROM signatories WHERE document_id = ? AND employee_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, documentId);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Signatory", e);
        }
    }
}
