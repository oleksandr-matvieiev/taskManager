package org.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TaskNotifier {

    public static void checkDeadlines(List<Task> tasks) {
        LocalDate now = LocalDate.now();

        for (Task task : tasks) {
            if (task.getEndDate() != null && task.getStatus()!=TaskStatus.DONE) {
                long daysLeft = ChronoUnit.DAYS.between(now, task.getEndDate());

                if (daysLeft == 3) {
                    Toast.showToast("Task \"" + task.getTitle() + "\" ends in 3 days!", 5000);
                } else if (daysLeft == 1) {
                    Toast.showToast("Task \"" + task.getTitle() + "\" ends tomorrow!", 5000);
                } else if (daysLeft == 0) {
                    Toast.showToast("Task \"" + task.getTitle() + "\" deadline is today!", 5000);
                }
            }
        }
    }
}
