package org.example;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
                    System.out.println("Enter Id of task you want remove");
                    int idToRemove = sc.nextInt();
                    taskManager.removeTask(idToRemove);
                    System.out.println("Task removed");
                    System.out.println();
                    break;
                case 3:
                    System.out.println("Which task completed?(id)");
                    int idCompleted = sc.nextInt();
                    taskManager.markTaskAsDone(idCompleted);
                    System.out.println("Good job");
                    System.out.println();
                    break;
                case 4:
                    List<Task> allTasks = taskManager.viewTasks();
                    allTasks.forEach(System.out::println);
                    System.out.println();
                    break;
                case 5:
                    System.out.println("Enter number of status for filter");
                    System.out.println("1. Failed");
                    System.out.println("2. In Progress");
                    System.out.println("3. Done");

                    TaskStatus status;

                    int a = sc.nextInt();

                    switch (a) {
                        case 1:
                            status = TaskStatus.FAILED;
                            break;
                        case 2:
                            status = TaskStatus.IN_PROGRESS;
                            break;
                        case 3:
                            status = TaskStatus.DONE;
                            break;
                        default:
                            System.out.println("Wrong option! Option Done will be used");
                            status = TaskStatus.DONE;
                            break;
                    }
                    List<Task> tasksByStatus = taskManager.viewTasksByStatus(status);
                    tasksByStatus.forEach(System.out::println);
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