package org.example.ui;

import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.TaskManager;
import org.example.service.TagManager;
import org.example.util.DateUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaskMenu {
    private final TaskManager taskManager;
    private final TagManager tagManager;
    private final Scanner sc;

    public TaskMenu(TaskManager taskManager, TagManager tagManager, Scanner sc) {
        this.taskManager = taskManager;
        this.tagManager = tagManager;
        this.sc = sc;
    }

    public void start() {
        boolean running = true;
        while (running) {
            SystemPrinter.info("--- Task Menu ---");
            SystemPrinter.info("1. Add task");
            SystemPrinter.info("2. Remove task");
            SystemPrinter.info("3. Mark task as completed");
            SystemPrinter.info("0. Back");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> addTask();
                case 2 -> removeTask();
                case 3 -> completeTask();
                case 0 -> running = false;
                default -> SystemPrinter.warn("Wrong number of option!");
            }
        }
    }

    public void viewAllTasks() {
        List<Task> tasks = taskManager.findAll();
        if (tasks.isEmpty()) {
            SystemPrinter.info("No tasks found.");
        } else {
            TaskPrinter.printTasks(tasks);
        }
    }

    private void addTask() {
        try {
            String title = readLine("Write title:");
            String description = readLine("Write description:");
            LocalDate date = DateUtils.parseDate(readLine("Write date (Format dd-MM-yyyy or ddMMyyyy )"));

            int interval = 0;
            SystemPrinter.info("""
                    Do you want to make this task repeatable?
                    1. Yes
                    2. No""");
            int repeatChoice = readInt("Your choice: ");
            if (repeatChoice == 1) {
                interval = readInt("Which interval? (Days): ");
            }

            Tag chosenTag = chooseTag();
            taskManager.save(new Task(title, description, date, interval, TaskStatus.IN_PROGRESS, chosenTag));
            SystemPrinter.success("Task added successfully!");
        } catch (DateTimeException e) {
            SystemPrinter.warn("Invalid date format: " + e.getMessage());
        }
    }

    private void removeTask() {
        List<Task> tasks = taskManager.findAll();
        Integer id = chooseTaskId(tasks, "Enter number of task you want to remove:");
        if (id != null) {
            taskManager.deleteTaskWithArchive(id);
            SystemPrinter.success("Task removed");
        }
    }

    private void completeTask() {
        List<Task> tasks = taskManager.findAll();
        Integer id = chooseTaskId(tasks, "Enter number of task which is completed: ");
        if (id != null) {
            taskManager.markAsDone(id);
            SystemPrinter.success("Good job!");
        }
    }

    private Tag chooseTag() {
        List<Tag> allTags = tagManager.findAll();
        if (allTags.isEmpty()) {
            SystemPrinter.info("No tags found. Please create a new one.");
            return tagManager.save(new Tag(readLine("Enter new tag name:")));
        }

        Map<Integer, Integer> tagMap = TagPrinter.printTags(allTags);
        int tagChoice = readInt("Enter tag number, or 0 to create new:");

        if (tagChoice == 0) {
            return tagManager.save(new Tag(readLine("Enter new tag name:")));
        }

        Integer tagId = tagMap.get(tagChoice);
        if (tagId == null) {
            SystemPrinter.warn("Invalid tag number. Using Uncategorized.");
            return tagManager.findAll().stream()
                    .filter(t -> "Uncategorized".equals(t.getName()))
                    .findFirst()
                    .orElse(new Tag("Uncategorized"));
        }

        return allTags.stream()
                .filter(t -> t.getId().equals(tagId))
                .findFirst()
                .orElseThrow();
    }

    private Integer chooseTaskId(List<Task> tasks, String prompt) {
        if (tasks.isEmpty()) {
            SystemPrinter.info("No tasks found.");
            return null;
        }
        Map<Integer, Integer> map = TaskPrinter.printTasks(tasks);
        int number = readInt(prompt);
        return map.get(number);
    }

    private int readInt(String message) {
        while (true) {
            try {
                SystemPrinter.info(message);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                SystemPrinter.warn("Please enter a valid number!");
            }
        }
    }

    private String readLine(String message) {
        SystemPrinter.info(message);
        return sc.nextLine().trim();
    }
}
