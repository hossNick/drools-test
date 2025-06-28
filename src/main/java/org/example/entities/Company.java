package org.example.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collation = "company")
public class Company {

    @Id
    private UUID id;
    private String name;
    @DBRef
    private List<CompanyFare> fares;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CompanyFare> getFares() {
        if (fares==null)
            fares = new ArrayList<CompanyFare>();
        return fares;
    }

    public void setFares(List<CompanyFare> fares) {
        this.fares = fares;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
