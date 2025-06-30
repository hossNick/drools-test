package org.example.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Car {

    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private String name;
    private String color;
    private Long price;
    @ManyToOne
    @JoinColumn(name = "company")
    private Company company;
    private Long fare;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getFare() {
        return fare;
    }

    public void setFare(Long fare) {
        this.fare = fare;
    }
}
