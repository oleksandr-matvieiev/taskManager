package org.example.ui;

import org.example.model.Tag;
import org.example.service.AppConfig;
import org.example.service.ExportService;
import org.example.service.TagManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SettingsMenu {
    private final TagManager tagManager;
    private final ExportService exportService;
    private final Scanner sc;
    private final AppConfig appConfig;

    public SettingsMenu(TagManager tagManager, ExportService exportService, AppConfig appConfig, Scanner sc) {
        this.tagManager = tagManager;
        this.exportService = exportService;
        this.sc = sc;
        this.appConfig = appConfig;
    }

    public void start() {
        boolean running = true;
        while (running) {
            SystemPrinter.info("--- Settings ---");
            SystemPrinter.info("1. Delete tag");
            SystemPrinter.info("2. Export to csv");
            SystemPrinter.info("3. Export to json");
            SystemPrinter.info("4. Change default dir ");
            SystemPrinter.info("5. Change days after delete task");

            SystemPrinter.info("0. Back");

            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> deleteTag();
                case 2 -> exportToCsv();
                case 3 -> exportToJson();
                case 4 -> changeExportDir();
                case 5 -> changeDaysAfterDeleteDone();
                case 0 -> running = false;
                default -> SystemPrinter.warn("Wrong choice. Try again.");
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

    private void exportToCsv() {
        try {
            exportService.exportToCsv();
            SystemPrinter.success("Exported to CSV");
        } catch (Exception e) {
            SystemPrinter.error("Error exporting to CSV: " + e.getMessage());
        }
    }

    private void exportToJson() {
        try {
            exportService.exportToJson();
            SystemPrinter.success("Exported to JSON");
        } catch (Exception e) {
            SystemPrinter.error("Error exporting to JSON: " + e.getMessage());
        }
    }

    private void changeExportDir() {
        SystemPrinter.info("Current export directory: " + exportService.getExportDir());

        String newExportDir = readString("Enter new export directory (leave blank to reset to default): ");

        try {
            exportService.changeExportDir(newExportDir);
            SystemPrinter.success("Export directory changed to: " + exportService.getExportDir());
        } catch (Exception e) {
            SystemPrinter.error("Failed to change export directory: " + e.getMessage());
        }
    }

    private void changeDaysAfterDeleteDone() {
        SystemPrinter.info("Current days before delete task with status: DONE - " + appConfig.getDeleteDoneAfterDays());

        Integer newDaysAfterDeleteDone = readInt("Enter new value: ");

        try {
            appConfig.changeDeleteDoneAfterDays(newDaysAfterDeleteDone);
            SystemPrinter.success("Days after delete task changed to: " + appConfig.getDeleteDoneAfterDays());
        }catch (Exception e) {
            SystemPrinter.error("Failed to change days after delete task: " + e.getMessage());
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

    private String readString(String message) {
        while (true) {
            try {
                SystemPrinter.info(message);
                return sc.nextLine().trim();
            } catch (Exception e) {
                SystemPrinter.warn("Please enter a valid text!");
            }
        }
    }
}
