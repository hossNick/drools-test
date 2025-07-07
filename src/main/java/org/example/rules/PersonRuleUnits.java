package org.example.rules;

import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.example.dto.PersonDto;
import org.example.service.PersonService;

import java.util.HashSet;
import java.util.Set;

public class PersonRuleUnits implements RuleUnitData {

//    @Inject
    private final PersonService personService;
    private DataStream<PersonDto> persons;
    private Set<String> errors;

    public PersonRuleUnits(PersonService personService) {
        this.personService = personService;
        errors = new HashSet<>();
    }

    public PersonService getPersonService() {
        return personService;
    }

    public DataStream<PersonDto> getPersons() {
        return persons;
    }

    public Set<String> getErrors() {
        if (errors == null)
            errors = new HashSet<>();
        return errors;
    }

    public void setPersons(PersonDto person) {
        persons.append(person);
    }

    public void addError(String error) {
        errors.add(error);
    }

}
