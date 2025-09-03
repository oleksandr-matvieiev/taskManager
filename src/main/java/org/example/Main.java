package org.example;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskDAO taskDAO = new TaskDAO();
        TaskManager taskManager = new TaskManager(taskDAO);
        Scanner sc = new Scanner(System.in);

        System.out.println("Hello its task manager. Please chose option\n");
        while (true) {
            System.out.println("1. Add task");
            System.out.println("2. Remove task");
            System.out.println("3. Mark task as completed");
            System.out.println("4. View all tasks");
            System.out.println("5. View tasks by status");
            System.out.println("0. Exit ");

            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    try {
                        System.out.println("Write title:");
                        String title = sc.nextLine();
                        System.out.println("Write description:");
                        String description = sc.nextLine();
                        System.out.println("Write date (Format dd-MM-yyyy or ddMMyyy )");
                        LocalDate date = formatDate(sc.next());
                        taskManager.addTask(new Task(title, description, date, TaskStatus.IN_PROGRESS));

                        System.out.println("Task added successfully!");
                    } catch (DateTimeException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println();
                    break;
                case 2:
                    List<Task> tasksToRemove = taskManager.viewTasks();
                    Map<Integer, Integer> deleteMap = TaskPrinter.printTasks(tasksToRemove);

                    System.out.println("Enter number of task you want to remove:");
                    int numberToRemove = sc.nextInt();

                    Integer id = deleteMap.get(numberToRemove);

                    if (id != null) {
                        taskManager.removeTask(id);
                        System.out.println("Task removed");
                    } else {
                        System.out.println("Invalid task number");
                    }
                    System.out.println();
                    break;
                case 3:
                    List<Task> tasksToComplete = taskManager.viewTasks();
                    Map<Integer, Integer> taskToMarkAsCompleted = TaskPrinter.printTasks(tasksToComplete);

                    System.out.println("Enter number of task which is completed:)");
                    int numberCompleted = sc.nextInt();
                    Integer idCompleted = taskToMarkAsCompleted.get(numberCompleted);
                    taskManager.markTaskAsDone(idCompleted);
                    System.out.println("Good job!");
                    System.out.println();
                    break;
                case 4:
                    List<Task> allTasks = taskManager.viewTasks();
                    TaskPrinter.printTasks(allTasks);
                    System.out.println();
                    break;
                case 5:
                    System.out.println("Enter number of status for filter");
                    System.out.println("1. Failed");
                    System.out.println("2. In Progress");
                    System.out.println("3. Done");

                    TaskStatus status;

                    int a = sc.nextInt();

                    status = switch (a) {
                        case 1 -> TaskStatus.FAILED;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.DONE;
                        default -> {
                            System.out.println("Wrong option! Option Done will be used");
                            yield TaskStatus.DONE;
                        }
                    };
                    List<Task> tasksByStatus = taskManager.viewTasksByStatus(status);
                    TaskPrinter.printTasks(tasksByStatus);
                    System.out.println();
                    break;
                case 0:
                    System.out.println("Bye");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Wrong option num");
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