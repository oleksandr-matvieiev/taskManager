package org.example;

import org.example.service.TaskManager;
import org.example.ui.TaskUI;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        TaskUI ui = new TaskUI();

        taskManager.updateTaskStatus();
        taskManager.checkDeadlines();
        ui.start();
    }
}