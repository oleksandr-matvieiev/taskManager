package org.example;

import org.example.dao.Database;
import org.example.dao.TagDAO;
import org.example.dao.TaskDAO;
import org.example.service.*;
import org.example.ui.TaskUI;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database.createTables();
        AppConfig appConfig = AppConfig.load();


        TaskDAO taskDAO = new TaskDAO();
        TagDAO tagDAO = new TagDAO();
        TagManager tagManager = new TagManager(tagDAO);
        TaskManager taskManager = new TaskManager(taskDAO, List.of(new SwingNotifier()),appConfig);
        ExportService exportService = new ExportService(taskManager, appConfig);

        taskManager.updateExpiredTasks();
        taskManager.notifyTasks();

        TaskUI ui = new TaskUI(taskManager, tagManager, exportService, appConfig);
        ui.start();
    }
}