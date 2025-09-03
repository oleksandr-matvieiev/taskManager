package org.example;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;

public class TaskManager {
    private final TaskDAO taskDAO;
    private final Validator validator;

    public TaskManager() {
        this.taskDAO = new TaskDAO();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public void addTask(Task task) {
        var violations = validator.validate(task);
        if (!violations.isEmpty()) {
            violations.forEach(System.out::println);
            return;
        }
        taskDAO.addTask(task);
    }

    public List<Task> viewTasks() {
        return taskDAO.getTasks();
    }

    public void removeTask(int id) {
        taskDAO.deleteTask(id);
    }

    public void markTaskAsDone(int id) {
        taskDAO.markTaskAsDone(id);

    }

    public List<Task> viewTasksByStatus(TaskStatus status) {
        return taskDAO.getTasksByStatus(status);
    }


    public void  checkDeadlines(){
        List<Task> tasks = taskDAO.getTasks();
        TaskNotifier.checkDeadlines(tasks);
    }
}
