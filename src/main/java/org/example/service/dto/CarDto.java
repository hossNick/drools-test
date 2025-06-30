package org.example.service.dto;

import java.util.UUID;

public class CarDto {

    private String code;
    private String name;
    private String color;
    private Long price;
    private UUID company;
    private Long fare;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public UUID getCompany() {
        return company;
    }

    public void setCompany(UUID company) {
        this.company = company;
    }

    public Long getFare() {
        return fare;
    }

    public void setFare(Long fare) {
        this.fare = fare;
    }
}
