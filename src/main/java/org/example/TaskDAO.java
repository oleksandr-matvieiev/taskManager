package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private Connection connection() {
        return Database.connect();
    }


    public void addTask(Task task) {
        String sql = "INSERT INTO tasks(title, description, endDate, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = connection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setDate(3, Date.valueOf(task.getEndDate()));
            preparedStatement.setString(4, task.getStatus().toString());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> getTasks() {
        String sql = "SELECT id, title, description,endDate, status from tasks";
        List<Task> taskList = new ArrayList<>();
        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {

            mapTasksFromDB(taskList, resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return taskList;
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Task deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void markTaskAsDone(int id) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection connection = connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, TaskStatus.DONE.toString());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        String sql = "SELECT id, title, description, endDate, status FROM tasks WHERE status = ?";
        List<Task> taskList = new ArrayList<>();

        try (Connection connection = connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            mapTasksFromDB(taskList, resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return taskList;
    }

    public void getAndUpdateTasks() {
        String sql = "SELECT id, endDate, status FROM tasks";

        try (Connection conn = connection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setEndDate(rs.getDate("endDate").toLocalDate());
                TaskStatus currentStatus = TaskStatus.valueOf(rs.getString("status"));

                if (task.getEndDate().isBefore(LocalDate.now()) && currentStatus != TaskStatus.DONE) {
                    task.setStatus(TaskStatus.FAILED);

                    String updateSql = "UPDATE tasks SET status = ? WHERE id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                        ps.setString(1, TaskStatus.FAILED.toString());
                        ps.setInt(2, task.getId());
                        ps.executeUpdate();
                    }
                } else {
                    task.setStatus(currentStatus);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void mapTasksFromDB(List<Task> taskList, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Task task = new Task();
            task.setId(resultSet.getInt("id"));
            task.setTitle(resultSet.getString("title"));
            task.setDescription(resultSet.getString("description"));
            task.setEndDate(resultSet.getDate("endDate").toLocalDate());
            task.setStatus(TaskStatus.valueOf(resultSet.getString("status")));
            taskList.add(task);
        }
    }

}
