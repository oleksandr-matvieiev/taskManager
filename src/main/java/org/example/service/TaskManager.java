package org.example.service;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dao.TaskDAO;
import org.example.model.Task;
import org.example.model.TaskStatus;

import java.util.List;

public class TaskManager {
    private final TaskDAO taskDAO;
    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();
    private final List<TaskNotifier> taskNotifiers;

    public TaskManager(TaskDAO taskDAO, List<TaskNotifier> taskNotifiers) {
        this.taskNotifiers = taskNotifiers;
        this.taskDAO = taskDAO;
    }


    public void save(Task task) {
        var violations = VALIDATOR.validate(task);
        if (!violations.isEmpty()) {
            violations.forEach(System.out::println);
            return;
        }
        taskDAO.save(task);
    }

    public List<Task> findAll() {
        return taskDAO.findAll();
    }

    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }
        taskDAO.deleteById(id);
    }

    public void markAsDone(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        } else {
            System.out.println("Invalid task ID");
        }

    }

    public List<Task> findByStatus(TaskStatus status) {
        return taskDAO.findByStatus(status);
    }


    public void notifyTasks() {
        List<Task> tasks = taskDAO.findAll();
        taskNotifiers.forEach(taskNotifier -> taskNotifier.notifyTasks(tasks));
    }

    public void updateExpiredTasks() {
        taskDAO.updateExpiredTasks();
    }
}
