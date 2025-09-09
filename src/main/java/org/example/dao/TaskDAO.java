package org.example.dao;

import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private final static Logger log = LoggerFactory.getLogger(TaskDAO.class.getName());

    private Connection connection() {
        return Database.connect();
    }

    private static final String BASE_SELECT =
            "SELECT t.id, t.title, t.description, t.endDate, " +
                    "t.repeatIntervalDays, t.status, t.tag_id, tg.name AS tag_name " +
                    "FROM tasks t " +
                    "LEFT JOIN tags tg ON t.tag_id = tg.id";

    // --- CRUD ---

    public void save(Task task) {
        String sql = "INSERT INTO tasks(title, description, endDate, repeatIntervalDays, status, tag_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getEndDate().toString());
            ps.setInt(4, task.getRepeatIntervalDays());
            ps.setString(5, task.getStatus().toString());
            ps.setInt(6, task.getTag().getId());

            ps.executeUpdate();
            log.info("Task saved: {}", task.getTitle());
        } catch (SQLException e) {
            log.error("Error while saving task", e);
            throw new RuntimeException("Failed to save task", e);
        }
    }

    public Task findById(int id) {
        String sql = BASE_SELECT + " WHERE t.id = ?";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapTaskFromResultSet(rs);
            }
        } catch (SQLException e) {
            log.error("Error fetching task by ID {}", id, e);
            throw new RuntimeException("Failed to fetch task by ID", e);
        }
        return null;
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT)) {

            mapTasksFromDB(tasks, rs);
            log.debug("Fetched {} tasks from DB", tasks.size());
        } catch (SQLException e) {
            log.error("Error while fetching tasks", e);
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
            log.debug("Fetched {} tasks by status {}", tasks.size(), status);
        } catch (SQLException e) {
            log.error("Error fetching tasks by status {}", status, e);
            throw new RuntimeException("Failed to fetch tasks by status", e);
        }
        return tasks;
    }

    public void deleteById(int id) {
        Task task = findById(id);
        if (task != null) {
            String sql = "DELETE FROM tasks WHERE id = ?";
            try (Connection conn = connection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                log.info("Task deleted: {}", id);
            } catch (SQLException e) {
                log.error("Error deleting task {}", id, e);
                throw new RuntimeException("Failed to delete task", e);
            }
        }
    }

    public void markAsDone(int id) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TaskStatus.DONE.toString());
            ps.setInt(2, id);
            ps.executeUpdate();
            log.info("Task marked as done: {}", id);
        } catch (SQLException e) {
            log.error("Error marking task as done {}", id, e);
            throw new RuntimeException("Failed to mark task as done", e);
        }
    }

    // --- Update expired tasks with optional deletion of old DONE tasks ---
    public void updateExpiredTasks(int daysAfterDelete) {
        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(BASE_SELECT)) {

            while (rs.next()) {
                Task task = mapTaskFromResultSet(rs);

                if (isTaskExpired(task)) {
                    if (task.getStatus() != TaskStatus.DONE && task.getRepeatIntervalDays() == 0) {
                        markTaskAsFailed(conn, task.getId());
                    }

                    if (task.getStatus() == TaskStatus.DONE) {
                        if (task.getRepeatIntervalDays() > 0) {
                            moveRecurringTask(conn, task.getId(), task.getEndDate().plusDays(task.getRepeatIntervalDays()));
                        } else if (task.getEndDate().plusDays(daysAfterDelete).isBefore(LocalDate.now())) {
                            deleteTask(conn, task);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            log.error("Error updating expired tasks", e);
            throw new RuntimeException("Failed to update expired tasks", e);
        }
    }

    // --- Helper methods ---

    private boolean isTaskExpired(Task task) {
        return task.getEndDate().isBefore(LocalDate.now());
    }

    private void markTaskAsFailed(Connection conn, int taskId) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TaskStatus.FAILED.toString());
            ps.setInt(2, taskId);
            ps.executeUpdate();
            log.info("Task {} expired and marked as FAILED", taskId);
        }
    }

    private void moveRecurringTask(Connection conn, int taskId, LocalDate newEndDate) throws SQLException {
        String sql = "UPDATE tasks SET endDate = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEndDate.toString());
            ps.setString(2, TaskStatus.IN_PROGRESS.toString());
            ps.setInt(3, taskId);
            ps.executeUpdate();
            log.info("Recurring task {} moved to next interval", taskId);
        }
    }

    private void deleteTask(Connection conn, Task task) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getId());
            ps.executeUpdate();
            log.info("Task {} DONE older than retention period deleted", task.getId());
        }
    }

    private Task mapTaskFromResultSet(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        String endDateStr = rs.getString("endDate");
        if (endDateStr != null) {
            task.setEndDate(LocalDate.parse(endDateStr));
        }
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setRepeatIntervalDays(rs.getInt("repeatIntervalDays"));

        Tag tag = new Tag();
        tag.setId(rs.getInt("tag_id"));
        tag.setName(rs.getString("tag_name"));
        task.setTag(tag);

        return task;
    }

    private void mapTasksFromDB(List<Task> taskList, ResultSet rs) throws SQLException {
        while (rs.next()) {
            taskList.add(mapTaskFromResultSet(rs));
        }
    }
}
