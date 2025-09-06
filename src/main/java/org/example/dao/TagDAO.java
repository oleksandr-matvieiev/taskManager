package org.example.dao;

import org.example.model.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {
    private Connection connection() {
        return Database.connect();
    }

    private static final String BASE_SELECT = "SELECT * FROM tags";

    public Tag save(Tag tag) {
        String sql = "INSERT INTO tags(name) VALUES(?)";

        try (Connection connection = connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, tag.getName());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tag.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tag: ", e);
        }
        return tag;
    }

    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();
        try (Connection connection = connection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(BASE_SELECT);) {
            mapTagsFromDB(tags, resultSet);


        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch tags: ", e);
        }
        return tags;
    }
    public void deleteById(int id){
        if (id == 1) {
            throw new IllegalArgumentException("Default tag cannot be deleted");
        }

        String reassignSql = "UPDATE tasks SET tag_id = 1 WHERE tag_id = ?";
        try (Connection connection = connection();
             PreparedStatement reassignStmt = connection.prepareStatement(reassignSql)) {
            reassignStmt.setInt(1, id);
            reassignStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reassign tasks before tag deletion", e);
        }


        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection connection=connection();
        PreparedStatement preparedStatement=connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag: ", e);
        }
    }

    private void mapTagsFromDB(List<Tag> tags, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Tag tag = new Tag();
            tag.setId(resultSet.getInt("id"));
            tag.setName(resultSet.getString("name"));
            tags.add(tag);
        }
    }
}
