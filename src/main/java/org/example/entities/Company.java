package org.example.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Company {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    @OneToMany
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
