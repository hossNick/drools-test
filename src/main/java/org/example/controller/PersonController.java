package org.example.controller;

import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.service.PersonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;
    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @PostMapping
    public Person createPerson(@RequestParam PersonDto dto){
        return personService.createPerson(dto);
    }
}
