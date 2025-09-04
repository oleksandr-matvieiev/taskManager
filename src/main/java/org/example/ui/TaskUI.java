package org.example.ui;

import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.TaskManager;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaskUI {
    public void start() {
        TaskManager taskManager = new TaskManager();
        Scanner sc = new Scanner(System.in);

        SystemPrinter.success("Welcome to Task Manager. Please enter the option:");
        while (true) {
            SystemPrinter.info("1. Add task");
            SystemPrinter.info("2. Remove task");
            SystemPrinter.info("3. Mark task as completed");
            SystemPrinter.info("4. View all tasks");
            SystemPrinter.info("5. View tasks by status");
            SystemPrinter.info("0. Exit ");

            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    try {
                        SystemPrinter.info("Write title:");
                        String title = sc.nextLine();
                        SystemPrinter.info("Write description:");
                        String description = sc.nextLine();
                        SystemPrinter.info("Write date (Format dd-MM-yyyy or ddMMyyyy )");
                        LocalDate date = formatDate(sc.next());
                        taskManager.addTask(new Task(title, description, date, TaskStatus.IN_PROGRESS));

                        SystemPrinter.success("Task added successfully!");
                    } catch (DateTimeException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println();
                    break;
                case 2:
                    List<Task> tasksToRemove = taskManager.viewTasks();
                    Map<Integer, Integer> deleteMap = TaskPrinter.printTasks(tasksToRemove);

                    SystemPrinter.info("Enter number of task you want to remove:");
                    int numberToRemove = sc.nextInt();

                    Integer id = deleteMap.get(numberToRemove);

                    if (id != null) {
                        taskManager.removeTask(id);
                        SystemPrinter.success("Task removed");
                    } else {
                        SystemPrinter.warn("Invalid task number");
                    }
                    System.out.println();
                    break;
                case 3:
                    List<Task> tasksToComplete = taskManager.viewTasks();
                    Map<Integer, Integer> taskToMarkAsCompleted = TaskPrinter.printTasks(tasksToComplete);

                    SystemPrinter.info("Enter number of task which is completed:)");
                    int numberCompleted = sc.nextInt();
                    Integer idCompleted = taskToMarkAsCompleted.get(numberCompleted);
                    taskManager.markTaskAsDone(idCompleted);
                    SystemPrinter.success("Good job!");
                    System.out.println();
                    break;
                case 4:
                    List<Task> allTasks = taskManager.viewTasks();
                    TaskPrinter.printTasks(allTasks);
                    System.out.println();
                    break;
                case 5:
                    SystemPrinter.info("Enter number of status for filter");
                    SystemPrinter.info("1. Failed");
                    SystemPrinter.info("2. In Progress");
                    SystemPrinter.info("3. Done");

                    TaskStatus status;

                    int a = sc.nextInt();

                    status = switch (a) {
                        case 1 -> TaskStatus.FAILED;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.DONE;
                        default -> {
                            SystemPrinter.warn("Wrong option! Option Done will be used");
                            yield TaskStatus.DONE;
                        }
                    };
                    List<Task> tasksByStatus = taskManager.viewTasksByStatus(status);
                    TaskPrinter.printTasks(tasksByStatus);
                    System.out.println();
                    break;
                case 0:
                    SystemPrinter.success("Bye");
                    sc.close();
                    System.exit(0);
                default:
                    SystemPrinter.warn("Wrong number of option!");
                    break;
            }
        }
    }
    private static LocalDate formatDate(String input) {
        if (input.matches("\\d{8}")) {
            input = input.substring(0, 2) + "." +
                    input.substring(2, 4) + "." +
                    input.substring(4);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return LocalDate.parse(input, formatter);
    }
}
