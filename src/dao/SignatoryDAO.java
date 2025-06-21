package dao;

import database.DBConnection;
import entities.Signatory;
import entities.user.Employee;
import exceptions.SignatoryValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing Signatory records in the database.
 */
public class SignatoryDAO {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Inserts a new signatory row for the given document.
     *
     * @param documentId ID of the document
     * @param signatory  Signatory to insert; must not be null
     * @throws IllegalArgumentException if signatory is null
     * @throws RuntimeException         on SQL errors
     */
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

    /**
     * Reads a signatory record by document and employee IDs.
     *
     * @param documentId ID of the document
     * @param employeeId ID of the employee
     * @return Signatory instance or null if not found
     * @throws RuntimeException on SQL or validation errors
     */
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

    /**
     * Retrieves all signatories for a specific document.
     *
     * @param documentId ID of the document
     * @return list of Signatory instances (empty if none)
     * @throws RuntimeException on SQL or validation errors
     */
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

    /**
     * Updates the sign status for a given document and employee.
     *
     * @param documentId ID of the document
     * @param signatory  Signatory with updated status
     * @throws RuntimeException on SQL errors
     */
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

    /**
     * Deletes all signatories for a given document.
     *
     * @param documentId ID of the document
     * @throws RuntimeException on SQL errors
     */
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

    /**
     * Deletes a specific signatory record.
     *
     * @param documentId ID of the document
     * @param employeeId ID of the employee
     * @throws RuntimeException on SQL errors
     */
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
