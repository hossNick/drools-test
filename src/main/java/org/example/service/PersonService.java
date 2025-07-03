package org.example.service;

import org.example.config.BaseService;
import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.repositories.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends BaseService<PersonDto, Person> {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person createPerson(PersonDto personDto) {
        PersonDto dto = evaluateDto(personDto);
        if (!dto.getErrorDescription().isEmpty()) {
            for (String s : dto.getErrorDescription()) {
                throw new RuntimeException(s);
            }
        } else {
            Person person = new Person();
            person.setName(dto.getName());
            person.setEmail(dto.getEmail());
            return personRepository.save(person);
        }
        throw new RuntimeException("Error");
    }
}
