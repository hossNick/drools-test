package org.example.service;

import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.repositories.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person createPerson(PersonDto personDto) {

    }
}
