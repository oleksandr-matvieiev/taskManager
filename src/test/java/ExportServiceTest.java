import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.AppConfig;
import org.example.service.ExportService;
import org.example.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ExportServiceTest {

    @Test
    void exportToCsvAndJson_success(@TempDir File tempDir) throws Exception {
        TaskManager taskManager = mock(TaskManager.class);
        AppConfig appConfig = mock(AppConfig.class);

        String exportDir = tempDir.getAbsolutePath() + File.separator;
        when(appConfig.getExportDir()).thenReturn(exportDir);

        Tag tag = new Tag("Work");
        tag.setId(1);

        Task task = new Task();
        task.setTitle("Test task");
        task.setDescription("Test description");
        task.setEndDate(LocalDate.of(2025, 12, 1));
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setTag(tag);

        when(taskManager.findAll()).thenReturn(List.of(task));
        ExportService exportService = new ExportService(taskManager, appConfig);

        exportService.exportToCsv();
        exportService.exportToJson();

        File csvFile = new File(exportDir + "tasks.csv");
        File jsonFile = new File(exportDir + "tasks.json");

        assertTrue(csvFile.exists(), "CSV file should be created");
        assertTrue(jsonFile.exists(), "JSON file should be created");

        String csvContent = Files.readString(csvFile.toPath());
        assertTrue(csvContent.contains("Test task"));
        assertTrue(csvContent.contains("Work"));

        String jsonContent = Files.readString(jsonFile.toPath());
        assertTrue(jsonContent.contains("Test task"));
        assertTrue(jsonContent.contains("Work"));

        verify(taskManager, times(2)).findAll();
    }
}
