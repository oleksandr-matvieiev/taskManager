package org.example.ui;

import org.example.model.Tag;
import org.example.service.TagManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SettingsMenu {
    private final TagManager tagManager;
    private final Scanner sc;

    public SettingsMenu(TagManager tagManager, Scanner sc) {
        this.tagManager = tagManager;
        this.sc = sc;
    }

    public void start() {
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
}
