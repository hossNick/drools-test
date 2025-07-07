package org.example.service;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.entities.Rule;
import org.example.repositories.PersonRepository;
import org.example.rules.PersonRuleUnits;
import org.example.rules.RuleService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final RuleService ruleService;
    private final KieContainer container;

    public PersonService(PersonRepository personRepository, RuleService ruleService, KieContainer container) {
        this.personRepository = personRepository;
        this.ruleService = ruleService;
        this.container = container;
    }

    public Person createPerson(PersonDto personDto) {
        executeDynamicRules(personDto);
        if (personDto.getErrorDescription().size() > 1) {
            for (String s : personDto.getErrorDescription()) {
                throw new RuntimeException(s);
            }
        }
        Person person = createPerson(personDto);
        return personRepository.save(person);
    }


    private Person convertPersonDtoToPerson(PersonDto personDto) {
        Person person = new Person();
        person.setName(personDto.getName());
        person.setEmail(personDto.getEmail());
        person.setAddress(personDto.getAddress());
        person.setSurname(personDto.getSurname());
        person.setNationalId(personDto.getNationalId());
        return person;
    }

    private void executeDynamicRules(PersonDto dto) {
        // Get all active rules from DB
        List<Rule> rules = ruleService.findByType("Person");
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        List<String> ruleContents = rules.stream().map(Rule::toString).toList();
        String ruleAppended = String.join("\n", ruleContents);
        kfs.write(ResourceFactory.newByteArrayResource(ruleAppended.getBytes())
                .setSourcePath("src/main/resources/rules/dynamic-validation.drl"));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll(); // Compile all resources in KieFileSystem

        // Check for compilation errors
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Error compiling dynamic rules: " + kieBuilder.getResults().toString());
        }
        KieBase tempKieBase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

        container.getKieBase();

        RuleUnitProvider provider = RuleUnitProvider.get();

        PersonRuleUnits units = new PersonRuleUnits(this);

        try (RuleUnitInstance<PersonRuleUnits> instance = provider.createRuleUnitInstance(units)) {
            units.getPersons().append(dto);

            instance.fire();
        }

//        RuleUnitExecutor ruleUnitExecutor = RuleUnitExecutor.create().bind(tempKieBase);
//        PersonRuleUnits unit = new PersonRuleUnits(this);
//
//        try {
//            ruleUnitExecutor.run(unit);
//            unit.getPersons().append(dto);
//        } finally {
//            ruleUnitExecutor.dispose();
//        }
    }
}
