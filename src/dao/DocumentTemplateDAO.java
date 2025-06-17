package dao;

import database.DBConnection;
import entities.DocumentTemplate;
import exceptions.DocumentTemplateValidationException;
import exceptions.IllegalIdException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentTemplateDAO implements GenericDAO<DocumentTemplate> {

    @Override
    public void insert(DocumentTemplate entity) {
        if (entity == null) {
            throw new IllegalArgumentException("DocumentTemplate cannot be null");
        }

        String sql = "INSERT INTO document_templates(structure, title) VALUES (?, ?) RETURNING id";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getStructure());
            stmt.setString(2, entity.getTitle());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting DocumentTemplate", e);
        }
    }

    @Override
    public DocumentTemplate readById(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalIdException("Invalid id for DocumentTemplate");
        }

        String sql = "SELECT id, structure, title FROM document_templates WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DocumentTemplate(
                            rs.getInt("id"),
                            rs.getString("structure"),
                            rs.getString("title")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading DocumentTemplate by id", e);
        } catch (DocumentTemplateValidationException e) {
            throw new RuntimeException("Invalid data from DB for DocumentTemplate", e);
        }

        return null;
    }

    @Override
    public void update(DocumentTemplate entity) {
        if (entity == null) {
            throw new IllegalArgumentException("DocumentTemplate cannot be null");
        }

        String sql = "UPDATE document_templates SET structure = ?, title = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getStructure());
            stmt.setString(2, entity.getTitle());
            stmt.setInt(3, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating DocumentTemplate", e);
        }
    }

    @Override
    public void delete(DocumentTemplate entity) {
        if (entity == null) {
            throw new IllegalArgumentException("DocumentTemplate cannot be null");
        }

        String sql = "DELETE FROM document_templates WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting DocumentTemplate", e);
        }
    }

    public List<DocumentTemplate> readAll() {
        ArrayList<DocumentTemplate> templates = new ArrayList<>();

        String sql = "SELECT id, title FROM document_templates";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DocumentTemplate template = new DocumentTemplate(rs.getInt("id"),
                        rs.getString("title"), null);
                templates.add(template);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return templates;
    }
}
