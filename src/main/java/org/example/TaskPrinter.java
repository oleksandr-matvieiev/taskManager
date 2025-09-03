package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskPrinter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    /**
     * Prints tasks in a formatted table and returns a map
     * that links row numbers (as shown to the user) to real task IDs.
     */
    public static Map<Integer, Integer> printTasks(List<Task> tasks) {
        Map<Integer, Integer> rowToId = new HashMap<>();

        String separator = "+----+--------------------------------+----------------------------------------------------+------------+--------------+\n";
        String header = String.format("| %-2s | %-30s | %-50s | %-10s | %-12s |\n",
                "№", "Title", "Description", "End Date", "Status");

        System.out.print(separator);
        System.out.print(header);
        System.out.print(separator);

        int rowNumber = 1;
        for (Task task : tasks) {
            String statusColored = switch (task.getStatus()) {
                case DONE -> ConsoleColor.GREEN.wrap(task.getStatus().toString());
                case FAILED -> ConsoleColor.RED.wrap(task.getStatus().toString());
                case IN_PROGRESS -> ConsoleColor.YELLOW.wrap(task.getStatus().toString());
            };

            String dateColored = task.getEndDate() != null && task.getEndDate().isBefore(LocalDate.now())
                    ? ConsoleColor.RED.wrap(task.getEndDate().format(FORMATTER))
                    : ConsoleColor.CYAN.wrap(task.getEndDate() != null ? task.getEndDate().format(FORMATTER) : "");

            String row = String.format("| %-2d | %-30s | %-50s | %-10s | %-12s |\n",
                    rowNumber,
                    task.getTitle() != null ? task.getTitle() : "",
                    task.getDescription() != null ? task.getDescription() : "",
                    dateColored,
                    statusColored
            );

            System.out.print(row);
            rowToId.put(rowNumber, task.getId());
            rowNumber++;
        }
        System.out.print(separator);

        return rowToId;
    }
}


