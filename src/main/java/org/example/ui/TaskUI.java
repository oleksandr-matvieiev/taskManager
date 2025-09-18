package org.example.ui;

import org.example.service.AppConfig;
import org.example.service.ExportService;
import org.example.service.TaskManager;
import org.example.service.TagManager;

import java.util.Scanner;

public class TaskUI {

    private final Scanner sc = new Scanner(System.in);

    private final TaskMenu taskMenu;
    private final FilterMenu filterMenu;
    private final SettingsMenu settingsMenu;

    public TaskUI(TaskManager taskManager, TagManager tagManager, ExportService exportService, AppConfig appConfig) {
        this.taskMenu = new TaskMenu(taskManager, tagManager, sc);
        this.filterMenu = new FilterMenu(taskManager, tagManager, sc);
        this.settingsMenu = new SettingsMenu(tagManager,exportService,appConfig, sc);
    }

    public void start() {
        SystemPrinter.success("Welcome to Task Manager. Please enter the option:");
        boolean running = true;
        while (running) {
            SystemPrinter.info("1. Task operations");
            SystemPrinter.info("2. View all tasks");
            SystemPrinter.info("3. Filters");
            SystemPrinter.info("9. Settings");
            SystemPrinter.info("0. Exit ");

            int choice = readInt();
            switch (choice) {
                case 1 -> taskMenu.start();
                case 2 -> taskMenu.viewAllTasks();
                case 3 -> filterMenu.start();
                case 9 -> settingsMenu.start();
                case 0 -> {
                    SystemPrinter.success("Bye!");
                    running = false;
                }
                default -> SystemPrinter.warn("Wrong number of option!");
            }
        }
        sc.close();
    }

    private int readInt() {
        while (true) {
            try {
                SystemPrinter.info("Choose option: ");
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                SystemPrinter.warn("Please enter a valid number!");
            }
        }
    }
}
