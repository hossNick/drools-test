package org.example.controller;

import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;
    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @PostMapping
    public List<Person> createPerson(@RequestBody List<PersonDto> dto){
        return personService.createPersonList(dto);
    }
}
