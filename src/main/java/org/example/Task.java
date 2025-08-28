package org.example;

import java.time.LocalDate;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private final LocalDate endDate;
    private final String status;



    //Constructor for load from DB
    public Task(int id, String title, String description, LocalDate endDate, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.status = status;
    }




    @Override
    public String toString() {
        return "Id: " + id + "\nTitle: " + title + "\nDescription: "
                + description + "\nEndDate: " + endDate + "\nStatus: " + status;
    }
}
