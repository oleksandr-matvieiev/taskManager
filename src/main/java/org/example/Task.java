package org.example;


import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    @NotNull(message = "Task title is required!")
    @Size(min = 1, max = 50)
    private  String title;
    @Size(min = 1, max = 50)
    private  String description;
    @NotNull(message = "End date is required!")
    @FutureOrPresent
    private LocalDate endDate;
    @NotNull
    private  TaskStatus status;


    public Task() {}

    public Task(String title, String description, LocalDate endDate, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String separator = "+----+------------------------------+------------------------------+------------+----------+\n";
        String header = String.format("| %-2s | %-28s | %-28s | %-10s | %-8s |\n",
                "ID", "Title", "Description", "End Date", "Status");
        String row = String.format("| %-2d | %-28s | %-28s | %-10s | %-8s |\n",
                id,
                title != null ? title : "",
                description != null ? description : "",
                endDate != null ? endDate.format(formatter) : "",
                status != null ? status.toString() : ""
        );

        return separator + header + separator + row + separator;
    }

}
