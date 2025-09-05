package org.example.model;


import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class Task {
    private Integer id;
    @NotNull(message = "Task title is required!")
    @Size(min = 1, max = 30)
    private  String title;
    @Size(min = 1, max = 50)
    private  String description;
    @NotNull(message = "End date is required!")
    @FutureOrPresent
    private LocalDate endDate;
    @PositiveOrZero
    private int repeatIntervalDays;
    @NotNull
    private  TaskStatus status;


    public Task() {}

    public Task(String title, String description, LocalDate endDate,int repeatIntervalDays, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.repeatIntervalDays = repeatIntervalDays;
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

    public int getRepeatIntervalDays() {
        return repeatIntervalDays;
    }

    public void setRepeatIntervalDays(int repeatIntervalDays) {
        this.repeatIntervalDays = repeatIntervalDays;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
