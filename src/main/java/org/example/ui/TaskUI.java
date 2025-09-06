package org.example.ui;

import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.TagManager;
import org.example.service.TaskManager;
import org.example.util.DateUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaskUI {
    private final TaskManager taskManager;
    private final TagManager tagManager;
    private final Scanner sc = new Scanner(System.in);

    public TaskUI(TaskManager taskManager, TagManager tagManager) {
        this.taskManager = taskManager;
        this.tagManager = tagManager;
    }

    public void start() {
        SystemPrinter.success("Welcome to Task Manager. Please enter the option:");
        while (true) {
            SystemPrinter.info("1. Add task");
            SystemPrinter.info("2. Remove task");
            SystemPrinter.info("3. Mark task as completed");
            SystemPrinter.info("4. View all tasks");
            SystemPrinter.info("5. View tasks by status");
            SystemPrinter.info("9. Settings");
            SystemPrinter.info("0. Exit ");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> addTask();
                case 2 -> removeTask();
                case 3 -> completeTask();
                case 4 -> viewAllTasks();
                case 5 -> viewByStatus();
                case 9 -> settingsMenu();
                case 0 -> exit();
                default -> SystemPrinter.warn("Wrong number of option!");
            }
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
            taskManager.deleteById(id);
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

    private void viewAllTasks() {
        List<Task> tasks = taskManager.findAll();
        if (tasks.isEmpty()) {
            SystemPrinter.info("No tasks found.");
        } else {
            TaskPrinter.printTasks(tasks);
        }
    }

    private void viewByStatus() {
        SystemPrinter.info("Enter number of status for filter");
        SystemPrinter.info("1. Failed");
        SystemPrinter.info("2. In Progress");
        SystemPrinter.info("3. Done");

        int choice = readInt("Choose: ");
        TaskStatus status = switch (choice) {
            case 1 -> TaskStatus.FAILED;
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.DONE;
            default -> {
                SystemPrinter.warn("Wrong option! Using Done.");
                yield TaskStatus.DONE;
            }
        };

        List<Task> tasks = taskManager.findByStatus(status);
        if (tasks.isEmpty()) {
            SystemPrinter.info("No tasks with status " + status);
        } else {
            TaskPrinter.printTasks(tasks);
        }
    }

    private void exit() {
        SystemPrinter.success("Bye");
        sc.close();
        System.exit(0);
    }

    // ---------- Settings Menu ----------
    private void settingsMenu() {
        boolean running = true;
        while (running) {
            SystemPrinter.info("--- Settings ---");
            SystemPrinter.info("1. Delete tag");
            SystemPrinter.info("0. Back");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> deleteTag();
                case 0 -> running = false;
                default -> SystemPrinter.warn("Wrong option!");
            }
        }
    }

    private void deleteTag() {
        List<Tag> tags = tagManager.findAll();
        if (tags.isEmpty()) {
            SystemPrinter.info("No tags found.");
            return;
        }

        Map<Integer, Integer> tagMap = TagPrinter.printTags(tags);
        int delChoice = readInt("Enter tag number to delete (0 - cancel): ");
        if (delChoice == 0) return;

        Integer tagId = tagMap.get(delChoice);
        if (tagId == null || tagId == 1) {
            SystemPrinter.warn("Invalid choice or can't delete 'Uncategorized'");
            return;
        }

        tagManager.deleteById(tagId);
        SystemPrinter.success("Tag deleted (tasks moved to 'Uncategorized')");
    }

    // ---------- Helpers ----------
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
}
