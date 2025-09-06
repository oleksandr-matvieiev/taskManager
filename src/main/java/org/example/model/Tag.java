package org.example.model;

import jakarta.validation.constraints.NotNull;

public class Tag {
    private Integer id;
    @NotNull
    private String name;


    public Tag(String name) {
        this.name = name;
    }
    public Tag(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
