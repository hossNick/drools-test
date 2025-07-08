package org.example.controller;

import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.service.PersonService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;
    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @PostMapping
    public Person createPerson(@RequestBody PersonDto dto){
        return personService.createPerson(dto);
    }
}
