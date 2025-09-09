package org.example.service;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dao.TaskDAO;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskManager {

    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

    private final TaskDAO taskDAO;
    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();
    private final List<TaskNotifier> taskNotifiers;
    private final AppConfig appConfig;

    public TaskManager(TaskDAO taskDAO, List<TaskNotifier> taskNotifiers, AppConfig appConfig) {
        this.taskNotifiers = taskNotifiers;
        this.taskDAO = taskDAO;
        this.appConfig = appConfig;
    }


    public void save(Task task) {
        var violations = VALIDATOR.validate(task);
        if (!violations.isEmpty()) {
            violations.forEach(v->log.warn("Validation error: {}",v.getMessage()));
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
        }
        taskDAO.markAsDone(id);

    }

    public List<Task> findByStatus(TaskStatus status) {
        return taskDAO.findByStatus(status);
    }


    public void notifyTasks() {
        List<Task> tasks = taskDAO.findAll();
        taskNotifiers.forEach(taskNotifier -> taskNotifier.notifyTasks(tasks));
        log.debug("Notified {} tasks via {} notifiers", tasks.size(), taskNotifiers.size());
    }

    public void updateExpiredTasks() {
        taskDAO.updateExpiredTasks(appConfig.getDeleteDoneAfterDays());
    }
}
