package org.example;

import org.example.dao.Database;
import org.example.dao.TaskDAO;
import org.example.service.SwingNotifier;
import org.example.service.TaskManager;
import org.example.ui.TaskUI;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database.createTables();
        TaskDAO taskDAO = new TaskDAO();
        TaskManager taskManager = new TaskManager(taskDAO, List.of(new SwingNotifier()));

        taskManager.updateExpiredTasks();
        taskManager.notifyTasks();

        TaskUI ui = new TaskUI(taskManager);
        ui.start();
    }
}