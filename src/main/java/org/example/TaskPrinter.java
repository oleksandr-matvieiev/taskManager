package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TaskPrinter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void printTasks(List<Task> tasks) {
        String separator = "+----+--------------------------------+----------------------------------------------------+------------+--------------+\n";
        String header = String.format("| %-2s | %-30s | %-50s | %-10s | %-12s |\n",
                "ID", "Title", "Description", "End Date", "Status");

        System.out.print(separator);
        System.out.print(header);
        System.out.print(separator);

        for (Task task : tasks) {
            String statusColored = switch (task.getStatus()) {
                case DONE -> ConsoleColor.GREEN.wrap(task.getStatus().toString());
                case FAILED -> ConsoleColor.RED.wrap(task.getStatus().toString());
                case IN_PROGRESS -> ConsoleColor.YELLOW.wrap(task.getStatus().toString());
            };

            String dateColored = task.getEndDate() != null && task.getEndDate().isBefore(LocalDate.now())
                    ? ConsoleColor.RED.wrap(task.getEndDate().format(FORMATTER))
                    : ConsoleColor.CYAN.wrap(task.getEndDate() != null ? task.getEndDate().format(FORMATTER) : "");

            String row = String.format("| %-2d | %-30s | %-50s | %-10s | %-8s |\n",
                    task.getId(),
                    task.getTitle() != null ? task.getTitle() : "",
                    task.getDescription() != null ? task.getDescription() : "",
                    dateColored,
                    statusColored
            );

            System.out.print(row);
        }
        System.out.print(separator);
    }
}


