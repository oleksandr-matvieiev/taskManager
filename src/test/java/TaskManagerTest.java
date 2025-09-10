import org.example.dao.TaskDAO;
import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class TaskManagerTest {
    private TaskDAO taskDAO;
    private List<TaskNotifier> taskNotifiers;
    private DeletedTaskArchiveService deletedTaskArchiveService;
    private AppConfig appConfig;
    private TaskManager taskManager;

    private Task invalidTask;
    private Task validTask;
    private Task validTaskRepeatable;

    @BeforeEach
    void setUp() {
        taskDAO = mock(TaskDAO.class);
        taskNotifiers = Collections.singletonList(mock(SwingNotifier.class));
        deletedTaskArchiveService = mock(DeletedTaskArchiveService.class);
        appConfig = mock(AppConfig.class);
        taskManager = new TaskManager(
                taskDAO,
                taskNotifiers,
                appConfig,
                deletedTaskArchiveService
        );

        invalidTask = new Task("Invalid test title",
                "Invalid test desc",
                LocalDate.now().minusDays(1),
                0,
                TaskStatus.IN_PROGRESS,
                new Tag("Tag"));
        invalidTask.setId(1);

        validTask = new Task("Test title",
                "Test desc",
                LocalDate.now().plusDays(1),
                0,
                TaskStatus.IN_PROGRESS,
                new Tag("Tag"));
        validTask.setId(2);

        validTaskRepeatable = new Task("Test title",
                "Test desc",
                LocalDate.now().plusDays(1),
                1,
                TaskStatus.IN_PROGRESS,
                new Tag("Tag"));
        validTaskRepeatable.setId(3);


    }

    @Test
    void saveTask_success() {
        taskManager.save(validTask);

        verify(taskDAO).save(validTask);
    }

    @Test
    void saveTask_failed_validationError() {
        taskManager.save(invalidTask);

        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    void findAllTasks_success() {
        List<Task> tasks = Collections.singletonList(validTask);

        when(taskDAO.findAll()).thenReturn(tasks);

        List<Task> result = taskManager.findAll();

        assertEquals(tasks, result);
        verify(taskDAO, times(1)).findAll();
    }

    @Test
    void findAllTasks_failed_DBError() {
        when(taskDAO.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> taskManager.findAll());

        verify(taskDAO).findAll();
    }

    @Test
    void deleteTask_success() {
        when(taskDAO.findById(validTask.getId())).thenReturn(validTask);

        taskManager.deleteTaskWithArchive(validTask.getId());

        verify(taskDAO).findById(validTask.getId());
        verify(deletedTaskArchiveService).archiveTask(validTask);
        verify(taskDAO, times(1)).deleteById(validTask.getId());
    }

    @Test
    void deleteTask_failed_IdNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTaskWithArchive(null));
        verify(taskDAO, never()).deleteById(anyInt());
        verify(deletedTaskArchiveService, never()).archiveTask(any());
    }

    @Test
    void deleteTask_failed_TaskNull() {
        when(taskDAO.findById(anyInt())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> taskManager.deleteTaskWithArchive(invalidTask.getId()));
        verify(taskDAO, never()).deleteById(anyInt());
        verify(deletedTaskArchiveService, never()).archiveTask(any());
    }

    @Test
    void markAsDone_success() {
        when(taskDAO.findById(validTask.getId())).thenReturn(validTask);
        taskManager.markAsDone(validTask.getId());

        verify(taskDAO, times(1)).markAsDone(validTask.getId());
    }

    @Test
    void markAsDone_failed_idNull() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.markAsDone(null));
        verify(taskDAO, never()).markAsDone(anyInt());
    }

    @Test
    void findByStatus_success() {
        when(taskDAO.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(Collections.singletonList(validTask));
        List<Task> result = taskManager.findByStatus(TaskStatus.IN_PROGRESS);

        assertEquals(Collections.singletonList(validTask), result);
        verify(taskDAO, times(1)).findByStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void findByStatus_failed_DBError() {
        when(taskDAO.findByStatus(any(TaskStatus.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> taskManager.findByStatus(TaskStatus.IN_PROGRESS));
    }

    @Test
    void notifyTasks_success() {
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTask));
        taskManager.notifyTasks();

        verify(taskDAO, times(1)).findAll();
        verify(taskNotifiers.get(0),times(1)).notifyTasks(Collections.singletonList(validTask));
    }

    @Test
    void updateExpiredTasks_success_markAsFailed() {
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTask));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTask)).thenReturn(true);
        taskManager.updateExpiredTasks();

        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
        verify(taskDAO, times(1)).markTaskAsFailed(validTask.getId());
    }

    @Test
    void updateExpiredTasks_success_UpdateDeadline() {
        validTaskRepeatable.setStatus(TaskStatus.DONE);
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTaskRepeatable));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTaskRepeatable)).thenReturn(true);

        taskManager.updateExpiredTasks();


        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
        verify(taskDAO, times(1))
                .moveRecurringTask(validTaskRepeatable.getId(), validTaskRepeatable.getEndDate()
                        .plusDays(validTaskRepeatable.getRepeatIntervalDays()));
    }

    @Test
    void updateExpiredTasks_success_deleteIfDone() {
        validTask.setStatus(TaskStatus.DONE);
        validTask.setEndDate(LocalDate.now().minusDays(1));
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTask));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTask)).thenReturn(true);

        taskManager.updateExpiredTasks();

        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
        verify(deletedTaskArchiveService, times(1)).archiveTask(validTask);
        verify(taskDAO, times(1)).deleteById(validTask.getId());
    }

    @Test
    void updateExpiredTasks_success_allTasksAreNotExpired() {
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTask));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTask)).thenReturn(false);

        taskManager.updateExpiredTasks();

        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
        verify(taskDAO, never()).markTaskAsFailed(anyInt());
        verify(taskDAO, never()).moveRecurringTask(anyInt(), any(LocalDate.class));
        verify(deletedTaskArchiveService, never()).archiveTask(any(Task.class));
        verify(taskDAO, never()).deleteById(anyInt());
    }

    @Test
    void updateExpiredTasks_failed_markAsFailed_DBError() {
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTask));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTask)).thenReturn(true);

        doThrow(new RuntimeException("DB error")).when(taskDAO).markTaskAsFailed(anyInt());

        assertThrows(RuntimeException.class, () -> taskManager.updateExpiredTasks());
        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
    }

    @Test
    void updateExpiredTasks_failed_UpdateDeadline_DBError() {
        validTaskRepeatable.setStatus(TaskStatus.DONE);
        when(taskDAO.findAll()).thenReturn(Collections.singletonList(validTaskRepeatable));
        when(appConfig.getDeleteDoneAfterDays()).thenReturn(0);
        when(taskDAO.isTaskExpired(validTaskRepeatable)).thenReturn(true);

        doThrow(new RuntimeException("DB error"))
                .when(taskDAO).moveRecurringTask(anyInt(),any(LocalDate.class));

        assertThrows(RuntimeException.class, () -> taskManager.updateExpiredTasks());

        verify(taskDAO, times(1)).findAll();
        verify(appConfig, times(1)).getDeleteDoneAfterDays();
        verify(taskDAO, never()).markTaskAsFailed(anyInt());
    }
}
