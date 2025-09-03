package org.example;

public class Main {
    public static void main(String[] args) {
        TaskDAO taskDAO = new TaskDAO();
        TaskManager taskManager = new TaskManager();
        TaskUI ui = new TaskUI();

        ui.start();
    }
}