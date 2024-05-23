package org.example;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner sc = new Scanner(System.in);

        System.out.println("Hello its task manager. Please chose option\n");
        while (true) {
            System.out.println("1. Add task");
            System.out.println("2. Remove task");
            System.out.println("3. Mark task as completed");
            System.out.println("4. View all tasks");
            System.out.println("5. Viev tasks by status");
            System.out.println("0. Exit ");

            int choise = sc.nextInt();
            switch (choise) {
                case 1:
                    try {
                        System.out.println("Write title:");
                        String title = sc.next();
                        System.out.println("Write description:");
                        String description = sc.next();
                        System.out.println("Write date (Format YYYY-MM-DD)");
                        LocalDate date = LocalDate.parse(sc.next());
                        taskManager.addTask(title, description, date);
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
                    taskManager.makrAsCompleted(idCompleted);
                    System.out.println("Good job");
                    System.out.println();
                    break;
                case 4:
                    List<Task> allTasks = taskManager.vievTasks();
                    allTasks.forEach(System.out::println);
                    System.out.println();
                    break;
                case 5:
                    System.out.println("Enter status for filter(Pending/Completed");
                    String statusForFiltr = sc.next();
                    List<Task> tasksByStatus = taskManager.vievTasksByStatus(statusForFiltr);
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
}