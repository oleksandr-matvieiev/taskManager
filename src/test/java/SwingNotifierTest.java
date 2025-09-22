import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.SwingNotifier;
import org.example.ui.Toast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

public class SwingNotifierTest {
    Task task;
    SwingNotifier swingNotifier = new SwingNotifier();

    @BeforeEach
    void setUp() {

        task = new Task(
                "Test Task",
                "Desc",
                LocalDate.now(),
                0,
                TaskStatus.IN_PROGRESS,
                null
        );
    }

    @Test
    void notifyTasks_showsToast_today() {
        try (MockedStatic<Toast> toastMockedStatic = Mockito.mockStatic(Toast.class)) {
            swingNotifier.notifyTasks(List.of(task));

            toastMockedStatic.verify(() -> Toast.showToast("Task \"Test Task\" deadline is today!", 5000));
        }
    }

    @Test
    void notifyTasks_showsToast_tomorrow() {
        task.setEndDate(LocalDate.now().plusDays(1));

        try (MockedStatic<Toast> toastMockedStatic = Mockito.mockStatic(Toast.class)) {
            swingNotifier.notifyTasks(List.of(task));

            toastMockedStatic.verify(() -> Toast.showToast("Task \"Test Task\" ends tomorrow!", 5000));
        }
    }

    @Test
    void notifyTasks_showsToast_threeDaysLeft() {
        task.setEndDate(LocalDate.now().plusDays(3));

        try (MockedStatic<Toast> toastMockedStatic = Mockito.mockStatic(Toast.class)) {
            swingNotifier.notifyTasks(List.of(task));

            toastMockedStatic.verify(() -> Toast.showToast("Task \"Test Task\" ends in 3 days!", 5000));
        }
    }

    @Test
    void notifyTasks_showsToast_doesNothingTaskDone() {
        task.setStatus(TaskStatus.DONE);

        try (MockedStatic<Toast> toastMockedStatic = Mockito.mockStatic(Toast.class)) {
            swingNotifier.notifyTasks(List.of(task));

            toastMockedStatic.verifyNoInteractions();
        }
    }
}
