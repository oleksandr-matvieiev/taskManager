package org.example.service;

import org.example.ui.Toast;
import org.example.model.Task;
import org.example.model.TaskStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SwingNotifier implements TaskNotifier{
    private static final int TOAST_DURATION_MS = 5000;

    @Override
    public void notifyTasks(List<Task> tasks) {
        LocalDate now = LocalDate.now();

        for (Task task : tasks) {
            if (task.getEndDate() != null && task.getStatus()!= TaskStatus.DONE) {
                long daysLeft = ChronoUnit.DAYS.between(now, task.getEndDate());

                switch ((int) daysLeft) {
                    case 3 -> Toast.showToast("Task \"" + task.getTitle() + "\" ends in 3 days!", TOAST_DURATION_MS);
                    case 1 -> Toast.showToast("Task \"" + task.getTitle() + "\" ends tomorrow!", TOAST_DURATION_MS);
                    case 0 -> Toast.showToast("Task \"" + task.getTitle() + "\" deadline is today!", TOAST_DURATION_MS);
                }
            }
        }
    }
}
