package org.example;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate endDate;
    private String status;



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
                + description + "\nEndDate: " + endDate + "\nStatus: ";
    }
}
