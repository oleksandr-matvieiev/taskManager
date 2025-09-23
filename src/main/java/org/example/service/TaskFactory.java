package org.example.service;

import org.example.model.Tag;
import org.example.model.Task;
import org.example.model.TaskStatus;

import java.time.LocalDate;

public class TaskFactory {

    public Task createTask(Tag chosenTag) {
        String title = TaskInputReader.readValidTitle();
        String description = TaskInputReader.readDescription();
        LocalDate date = TaskInputReader.readValidDate();
        int interval = TaskInputReader.readRepeatInterval();

        return new Task(title, description, date, interval, TaskStatus.IN_PROGRESS, chosenTag);
    }
}
