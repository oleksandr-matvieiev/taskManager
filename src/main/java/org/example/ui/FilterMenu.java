package org.example.ui;

import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.TaskManager;
import org.example.service.TagManager;
import org.example.util.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FilterMenu {
    private final TaskManager taskManager;
    private final TagManager tagManager;
    private final Scanner sc;

    public FilterMenu(TaskManager taskManager, TagManager tagManager, Scanner sc) {
        this.taskManager = taskManager;
        this.tagManager = tagManager;
        this.sc = sc;
    }

    public void start() {
        boolean running = true;
        while (running) {
            SystemPrinter.info("--- Filters Menu ---");
            SystemPrinter.info("1. By status");
            SystemPrinter.info("2. By tag");
            SystemPrinter.info("3. By deadline");
            SystemPrinter.info("4. By keyword");
            SystemPrinter.info("0. Back");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> filterByStatus();
                case 2 -> filterByTag();
                case 3 -> filterByDeadline();
                case 4 -> filterByKeyword();
                case 0 -> running = false;
                default -> SystemPrinter.warn("Wrong option!");
            }
        }
    }

    private void filterByStatus() {
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
        printResult(tasks, "status " + status);
    }

    private void filterByTag() {
        List<Tag> tags = tagManager.findAll();
        if (tags.isEmpty()) {
            SystemPrinter.info("No tags found.");
            return;
        }

        Map<Integer, Integer> tagMap = TagPrinter.printTags(tags);
        int choice = readInt("Choose tag: ");
        Integer tagId = tagMap.get(choice);

        if (tagId == null) {
            SystemPrinter.warn("Wrong tag id!");
            return;
        }

        List<Task> tasks = taskManager.findAll().stream()
                .filter(t -> t.getTag() != null && t.getTag().getId().equals(tagId))
                .toList();
        printResult(tasks, "tag " + tagId);
    }

    private void filterByDeadline() {
        LocalDate date = DateUtils.parseDate(readLine("Enter deadline date: "));
        List<Task> tasks = taskManager.findAll().stream()
                .filter(t -> t.getEndDate() != null && t.getEndDate().isEqual(date))
                .toList();
        printResult(tasks, "deadline " + date);
    }

    private void filterByKeyword() {
        String keyword = readLine("Enter keyword: ").toLowerCase();
        List<Task> tasks = taskManager.findAll().stream()
                .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(keyword)) ||
                        (t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword)))
                .toList();
        printResult(tasks, "keyword '" + keyword + "'");
    }

    private void printResult(List<Task> tasks, String filter) {
        if (tasks.isEmpty()) {
            SystemPrinter.info("No tasks found by " + filter);
        } else {
            TaskPrinter.printTasks(tasks);
        }
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
