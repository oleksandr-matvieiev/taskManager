package org.example;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        TaskUI ui = new TaskUI();

        taskManager.updateTaskStatus();
        taskManager.checkDeadlines();
        ui.start();
    }
}