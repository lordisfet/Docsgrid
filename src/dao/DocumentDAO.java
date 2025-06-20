package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.DBConnection;
import entities.Document;
import entities.DocumentTemplate;
import entities.Signatory;
import exceptions.DocumentValidationException;
import exceptions.IllegalIdException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentDAO implements GenericDAO<Document> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DocumentTemplateDAO templateDAO = new DocumentTemplateDAO();
    private final SignatoryDAO signatoryDAO = new SignatoryDAO();

    @Override
    public void insert(Document entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        String sql = "INSERT INTO documents(template_id, content) VALUES (?, ?) RETURNING id";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getTemplate().getId());
            String json = mapper.writeValueAsString(entity.getContent());
            stmt.setString(2, json);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getInt("id"));
                }
            }

            for (Signatory signatory : entity.getSignatories()) {
                signatoryDAO.insert(entity.getId(), signatory);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Document", e);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @Override
    public Document readById(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException("Invalid Document id");
        }
        String sql = "SELECT template_id, content FROM documents WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                int tplId = rs.getInt("template_id");
                String json = rs.getString("content");

                DocumentTemplate tpl = templateDAO.readById(tplId);
                Map<String,String> content = mapper.readValue(json, new TypeReference<>(){});
                List<Signatory> signatories = signatoryDAO.findByDocumentId(id);

                return new Document(id, tpl, content, signatories);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading Document", e);
        } catch (DocumentValidationException e) {
            throw new RuntimeException("Invalid Document data", e);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }

    public List<Document> listBySignatoryEmployeeId(Integer employeeId, boolean signStatus) {
        if (employeeId == null || employeeId < 1) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        String sql = "SELECT d.id, d.template_id, d.content FROM documents d " +
                "JOIN signatories s ON s.document_id = d.id WHERE s.employee_id = ? AND sign_status = ?";

        List<Document> documents = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.setBoolean(2, signStatus);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int documentId = rs.getInt("id");
                    int tplId = rs.getInt("template_id");
                    String json = rs.getString("content");

                    DocumentTemplate tpl = templateDAO.readById(tplId);
                    Map<String, String> content = mapper.readValue(json, new TypeReference<>() {});

                    List<Signatory> signatories = signatoryDAO.findByDocumentId(documentId);

                    Document doc = new Document(tpl, content, signatories);
                    doc.setId(documentId);
                    documents.add(doc);
                }
            }

            return documents;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Document", e);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @Override
    public void update(Document entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        String sql = "UPDATE documents SET content = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String json = mapper.writeValueAsString(entity.getContent());
            stmt.setString(1, json);
            stmt.setInt(2, entity.getId());
            stmt.executeUpdate();

            for (Signatory signatory : entity.getSignatories()) {
                signatoryDAO.update(entity.getId(), signatory);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating Document", e);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @Override
    public void delete(Document entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        signatoryDAO.deleteByDocumentId(entity.getId());

        String sql = "DELETE FROM documents WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Document", e);
        }
    }
}
