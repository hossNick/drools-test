package org.example.service;

import org.example.config.BaseDto;
import org.example.config.BaseService;
import org.example.config.DroolsService;
import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService extends BaseService<PersonDto, Person> {

    private final PersonRepository personRepository;
    private final DroolsService droolsService;

    public PersonService(PersonRepository personRepository, DroolsService droolsService) {
        this.personRepository = personRepository;
        this.droolsService = droolsService;
    }

    public Person createPerson(PersonDto dto) {
//        PersonDto dto = evaluateDto(personDto);
        droolsService.validateByEntityName(dto);
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

    public List<Person> createPersonList(List<PersonDto> dto) {
        var dtoList = dto.stream().map(personDto -> (BaseDto) personDto).toList();
        droolsService.validateByEntityName(dtoList);


        var hasErrors = dtoList.stream().anyMatch(item -> !item.getErrorDescription().isEmpty());

        if (hasErrors) {

            var errorMessages = dtoList.stream()
                    .flatMap(item -> item.getErrorDescription().stream())
                    .toList();

            for (String error : errorMessages) {
                throw new RuntimeException(error);
            }
        } else {

            var personList = dto.stream().map(item -> {
                Person person = new Person();
                person.setName(item.getName());
                person.setEmail(item.getEmail());
                return person;
            }).collect(Collectors.toList());

            return Collections.emptyList();
//            return personRepository.saveAll(personList);
        }

        throw new RuntimeException("Error");
    }

}
