package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeletedTaskArchiveService {
    private static  final Logger log = LoggerFactory.getLogger(DeletedTaskArchiveService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");
    private static final String ARCHIVE_PATH = "archive/";

    private final ObjectMapper mapper;


    public DeletedTaskArchiveService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());


        File dir = new File(ARCHIVE_PATH);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("Created archive directory: {}", ARCHIVE_PATH);
            }
        }
    }

    public void archiveTask(Task task) {
        String monthFileName = ARCHIVE_PATH + "deleted_tasks_" + LocalDate.now().format(formatter) + ".json";
        File monthFile = new File(monthFileName);
        List<Task> archivedTasks=new ArrayList<>();

        if (monthFile.exists()) {
            try {
                Task[] existing = mapper.readValue(monthFile, Task[].class);
                archivedTasks.addAll(Arrays.asList(existing));
            }catch (IOException e){
                log.error("Error reading archive file", e);
            }
        }
        archivedTasks.add(task);

        try {
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(monthFile, archivedTasks);
            log.info("Task {} archived in file {}", task.getId(), monthFileName);
        } catch (IOException e) {
            log.error("Failed to write task archive file {}", monthFileName, e);
        }


    }
}
