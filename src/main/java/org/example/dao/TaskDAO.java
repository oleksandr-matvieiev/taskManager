package org.example.dao;

import org.example.model.Task;
import org.example.model.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private Connection connection() {
        return Database.connect();
    }

    private static final String BASE_SELECT =
            "SELECT id, title, description, endDate,repeatIntervalDays, status FROM tasks";

    public void save(Task task) {
        String sql = "INSERT INTO tasks(title, description, endDate,repeatIntervalDays, status) VALUES (?, ?, ?,?, ?)";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setDate(3, Date.valueOf(task.getEndDate()));
            ps.setInt(4, task.getRepeatIntervalDays());
            ps.setString(5, task.getStatus().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task", e);
        }
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT)) {

            mapTasksFromDB(tasks, rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch tasks", e);
        }
        return tasks;
    }

    public List<Task> findByStatus(TaskStatus status) {
        List<Task> tasks = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE status = ?";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toString());
            ResultSet rs = ps.executeQuery();
            mapTasksFromDB(tasks, rs);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch tasks by status", e);
        }
        return tasks;
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    public void markAsDone(int id) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.DONE.toString());
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task status", e);
        }
    }

    public void updateExpiredTasks() {
        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate endDate = rs.getDate("endDate").toLocalDate();
                TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
                int repeatIntervalDays = rs.getInt("repeatIntervalDays");

                if (endDate.isBefore(LocalDate.now())) {
                    if (status != TaskStatus.DONE && repeatIntervalDays == 0) {
                        String updateSql = "UPDATE tasks SET status = ? WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                            ps.setString(1, TaskStatus.FAILED.toString());
                            ps.setInt(2, id);
                            ps.executeUpdate();
                        }
                    }
                    if (status == TaskStatus.DONE && repeatIntervalDays > 0) {
                        String updateSql = "UPDATE tasks SET endDate = ?, status = ?  WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                            ps.setString(1, endDate.plusDays(repeatIntervalDays).toString());
                            ps.setString(2, TaskStatus.IN_PROGRESS.toString());
                            ps.setInt(3, id);
                            ps.executeUpdate();
                        }

                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expired tasks", e);
        }
    }

    private void mapTasksFromDB(List<Task> taskList, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Task task = new Task();
            task.setId(rs.getInt("id"));
            task.setTitle(rs.getString("title"));
            task.setDescription(rs.getString("description"));
            task.setEndDate(rs.getDate("endDate").toLocalDate());
            task.setStatus(TaskStatus.valueOf(rs.getString("status")));
            taskList.add(task);
        }
    }
}
