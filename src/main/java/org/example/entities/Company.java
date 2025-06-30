package org.example.entities;

import java.util.List;
import java.util.UUID;

public class Company {

    private UUID id;
    private String name;
    private List<Car> carList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
