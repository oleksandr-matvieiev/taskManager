package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private Connection connection() {
        return Database.connect();
    }

    public void addTask(String title, String description, LocalDate endDate) {
        String sql = "INSERT INTO tasks(title, description, endDate, status) values(?,?,?,?)";
        try (Connection conn = this.connection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setString(3, endDate.toString());
            statement.setString(4, "Pending");
            statement.executeUpdate();
            System.out.println("Task added " + title);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> vievTasks() {
        String sql = "SELECT id, title, description,endDate, status from tasks";
        List<Task> taskList = new ArrayList<>();
        try (Connection conn = this.connection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {

            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        LocalDate.parse(resultSet.getString("endDate")),
                        resultSet.getString("status")
                );
                taskList.add(task);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return taskList;
    }

    public void removeTask(int id) {
        String sql = "DELETE FROM tasks where id=?";

        try (Connection conn = this.connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Task removed");
    }

    public void makrAsCompleted(int id) {
        String sql = "UPDATE tasks set status = ? where id=?";


        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Completed");
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Status changed");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> vievTasksByStatus(String status) {
        String sql = "SELECT id,title,description,endDate,status from tasks where status=?";
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = connection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        LocalDate.parse(rs.getString("endDate")),
                        rs.getString("status")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }

}
