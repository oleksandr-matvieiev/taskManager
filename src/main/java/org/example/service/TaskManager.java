package org.example.service;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dao.TaskDAO;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class TaskManager {

    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

    private final TaskDAO taskDAO;
    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();
    private final List<TaskNotifier> taskNotifiers;
    private final AppConfig appConfig;
    private final DeletedTaskArchiveService deletedTaskArchiveService;

    public TaskManager(TaskDAO taskDAO, List<TaskNotifier> taskNotifiers, AppConfig appConfig, DeletedTaskArchiveService deletedTaskArchiveService) {
        this.taskNotifiers = taskNotifiers;
        this.taskDAO = taskDAO;
        this.appConfig = appConfig;
        this.deletedTaskArchiveService = deletedTaskArchiveService;
    }


    public void save(Task task) {
        var violations = VALIDATOR.validate(task);
        if (!violations.isEmpty()) {
            violations.forEach(v -> log.warn("Validation error: {}", v.getMessage()));
            return;
        }
        taskDAO.save(task);
    }

    public List<Task> findAll() {
        return taskDAO.findAll();
    }

    public void deleteTaskWithArchive(Integer id) {
        if (id == null) {
            log.error("Task id is null");
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        Task task = taskDAO.findById(id);
        if (task == null) {
            log.warn("Task {} not found for deletion", id);
            throw new IllegalArgumentException("Task ID not found for deletion");
        }

        deletedTaskArchiveService.archiveTask(task);

        taskDAO.deleteById(id);

        log.info("Task {} deleted and archived", id);
    }

    public void markAsDone(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }
        taskDAO.markAsDone(id);
        log.info("Task {} marked as done", id);

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
        List<Task> tasks = taskDAO.findAll();
        int retentionDays = appConfig.getDeleteDoneAfterDays();

        for (Task task : tasks) {
            if (taskDAO.isTaskExpired(task)) {
                handleExpiredTask(task, retentionDays);
            }
        }
    }

    private void handleExpiredTask(Task task, int retentionDays) {
        if (task.getStatus() != TaskStatus.DONE && task.getRepeatIntervalDays() == 0) {
            taskDAO.markTaskAsFailed(task.getId());

            log.info("Task {} marked as failed", task.getId());
        } else if (task.getStatus() == TaskStatus.DONE) {
            if (task.getRepeatIntervalDays() > 0) {

                taskDAO.moveRecurringTask(task.getId(), task.getEndDate().plusDays(task.getRepeatIntervalDays()));
                log.warn("Task {} become new deadline", task.getId());
            } else if (task.getEndDate().plusDays(retentionDays).isBefore(LocalDate.now())) {

                deletedTaskArchiveService.archiveTask(task);
                taskDAO.deleteById(task.getId());
            }
        }
    }


}
