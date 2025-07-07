package org.example.service;

import org.drools.ruleunit.RuleUnitExecutor;
import org.example.config.BaseService;
import org.example.dto.PersonDto;
import org.example.entities.Person;
import org.example.entities.Rule;
import org.example.repositories.PersonRepository;
import org.example.rules.PersonRuleUnits;
import org.example.rules.RuleService;
import org.kie.api.KieBase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService extends BaseService<PersonDto, Person> {

    private final PersonRepository personRepository;
    private final RuleService ruleService;
    private final RuleUnitExecutor executor;

    public PersonService(PersonRepository personRepository, RuleService ruleService
            , RuleUnitExecutor executor) {
        this.personRepository = personRepository;
        this.ruleService = ruleService;
        this.executor = executor;
    }

    public Person createPerson(PersonDto personDto) {
        PersonRuleUnits personRuleUnits= new PersonRuleUnits(this);

    }

    private void executeDynamicRules(PersonDto dto, PersonRuleUnits ruleUnit) {
        // Get all active rules from DB
        List<Rule> rules = ruleService.findByType("Person");

        // Create a temporary KieBase
        KieBase kieBase = createKieBaseFromRules(rules);

        // Execute
        executor.bind(kieBase);
        ruleUnit.getPersons().append(dto);
        executor.run(ruleUnit);
    }

    private KieBase createKieBaseFromRules(List<Rule> rules) {
        KieServices kieServices = KieServices.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        // Add each rule from DB
        rules.forEach(rule ->
                kfs.write(ResourceFactory.newByteArrayResource(rule.getContent().getBytes())
                        .setTargetPath(rule.getName() + ".drl")
                );

        // Build the KieBase
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        return kieBuilder.getKieBase();
    }
}
