package org.example;


import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class Task {
    private int id;
    @NotNull
    @Size(min = 1, max = 50)
    private  String title;
    @NotNull
    @Size(min = 1, max = 50)
    private  String description;
    @NotNull
    @Future
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
        return "Id: " + id + "\nTitle: " + title + "\nDescription: "
                + description + "\nEndDate: " + endDate + "\nStatus: " + status.toString();
    }
}
