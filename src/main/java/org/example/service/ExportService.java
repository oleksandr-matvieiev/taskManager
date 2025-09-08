package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.example.model.Task;
import com.opencsv.CSVWriter;
import org.example.util.TaskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final TaskManager taskManager;
    private String exportDir = System.getProperty("user.home") + "/Downloads/";

    public ExportService(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void exportToCsv() {
        List<Task> tasks = taskManager.findAll();
        Map<Integer, Task> numberedTasks = TaskUtils.enumerateTasks(tasks);

        String filePath = this.exportDir + "tasks.csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(new String[]{"Num", "Title", "Description", "EndDate", "Status", "Tag"});

            for (Map.Entry<Integer, Task> entry : numberedTasks.entrySet()) {
                Task task = entry.getValue();
                writer.writeNext(new String[]{
                        String.valueOf(entry.getKey()),
                        task.getTitle(),
                        task.getDescription(),
                        task.getEndDate().toString(),
                        task.getStatus().toString(),
                        task.getTag().getName()
                });
            }

            log.info("Task exported to CSV: {}", filePath);
        } catch (IOException e) {
            log.error("Error exporting tasks to file .csv", e);
            throw new RuntimeException(e);
        }
    }

    public void exportToJson() {
        List<Task> tasks = taskManager.findAll();
        String filePath = this.exportDir + "tasks.json";

        try {
            ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

            List<TaskJsonDTO> dtoList = tasks.stream()
                    .map(TaskJsonDTO::fromTask)
                    .toList();

            writer.writeValue(new File(filePath), dtoList);

            log.info("Tasks exported to JSON: {}", filePath);
        } catch (IOException e) {
            log.error("Error exporting tasks to file .json", e);
            throw new RuntimeException(e);
        }
    }

    public void changeExportDir(String newExportDir) {
        if (newExportDir == null || newExportDir.isBlank()) {
            this.exportDir = System.getProperty("user.home") + "/Downloads/";
        } else {
            this.exportDir = newExportDir.endsWith("/") ? newExportDir : newExportDir + "/";
        }
    }

    public String getExportDir() {
        return exportDir;
    }


    private static class TaskJsonDTO {
        public String title;
        public String description;
        public String endDate;
        public int repeatIntervalDays;
        public String status;
        public String tag;

        static TaskJsonDTO fromTask(Task task) {
            TaskJsonDTO dto = new TaskJsonDTO();
            dto.title = task.getTitle();
            dto.description = task.getDescription();
            dto.endDate = task.getEndDate().toString();
            dto.repeatIntervalDays = task.getRepeatIntervalDays();
            dto.status = task.getStatus().toString();
            dto.tag = task.getTag().getName();
            return dto;
        }
    }
}

