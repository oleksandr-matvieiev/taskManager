package org.example.service;

import org.example.ui.SystemPrinter;
import org.example.util.DateUtils;

import java.time.LocalDate;
import java.util.Scanner;

public class TaskInputReader {
    private static final Scanner sc = new Scanner(System.in);

    public static String readValidTitle() {
        String title = null;
        while (title == null || title.isBlank()) {
            SystemPrinter.info("Write title:");
            title = sc.nextLine().trim();
            if (title.isBlank()) {
                SystemPrinter.warn("Title cannot be empty. Please try again.");
            }
        }
        return title;
    }

    public static String readDescription() {
        SystemPrinter.info("Write description:");
        String description = sc.nextLine().trim();
        return description.isBlank() ? "No description." : description;
    }

    public static LocalDate readValidDate() {
        LocalDate date = null;
        while (date == null) {
            SystemPrinter.info("Write date (Format dd-MM-yyyy or ddMMyyyy): ");
            String input = sc.nextLine().trim();
            try {
                date = DateUtils.parseDate(input);
                if (date.isBefore(LocalDate.now())) {
                    SystemPrinter.warn("Date cannot be before now. Please try again.");
                    date = null;
                }
            } catch (IllegalArgumentException e) {
                SystemPrinter.warn(e.getMessage() + " Please try again.");
            }
        }
        return date;
    }

    public  static int readRepeatInterval() {
        SystemPrinter.info("""
                Do you want to make this task repeatable?
                1. Yes
                2. No""");
        int repeatChoice = 0;
        while (repeatChoice < 1 || repeatChoice > 2) {
            SystemPrinter.info("Your choice: ");
            try {
                repeatChoice = Integer.parseInt(sc.nextLine().trim());
                if (repeatChoice < 1 || repeatChoice > 2) {
                    SystemPrinter.warn("Wrong choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                SystemPrinter.warn("Please enter a valid number!");
            }
        }

        if (repeatChoice == 1) {
            while (true) {
                SystemPrinter.info("Which interval? (Days): ");
                try {
                    int interval = Integer.parseInt(sc.nextLine().trim());
                    if (interval < 1) {
                        SystemPrinter.warn("Invalid interval. Please try again.");
                    } else {
                        return interval;
                    }
                } catch (NumberFormatException e) {
                    SystemPrinter.warn("Please enter a valid number!");
                }
            }
        }

        return 0;
    }
}
