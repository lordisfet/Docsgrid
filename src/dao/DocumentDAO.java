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

                return new Document(tpl, content, signatories);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading Document", e);
        } catch (DocumentValidationException e) {
            throw new RuntimeException("Invalid Document data", e);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization error", e);
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
