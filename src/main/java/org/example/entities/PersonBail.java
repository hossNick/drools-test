package org.example.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class PersonBail {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;
    @ManyToOne
    @JoinColumn(name = "bail")
    private Bail bail;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Bail getBail() {
        return bail;
    }

    public void setBail(Bail bail) {
        this.bail = bail;
    }
}
