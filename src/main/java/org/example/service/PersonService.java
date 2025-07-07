package org.example.service;

import org.drools.ruleunit.RuleUnitExecutor;
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
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService  {

    private final PersonRepository personRepository;
    private final RuleService ruleService;

    public PersonService(PersonRepository personRepository, RuleService ruleService) {
        this.personRepository = personRepository;
        this.ruleService = ruleService;
    }

    public Person createPerson(PersonDto personDto) {


    }

    private void executeDynamicRules(PersonDto dto) {
        // Get all active rules from DB
        List<Rule> rules = ruleService.findByType("Person");
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        List<String> ruleContents= rules.stream().map(Rule::toString).toList();
        String ruleAppended= String.join("\n", ruleContents);
        kfs.write(ResourceFactory.newByteArrayResource(ruleAppended.getBytes())
                .setSourcePath("src/main/resources/rules/dynamic-validation.drl"));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll(); // Compile all resources in KieFileSystem

        // Check for compilation errors
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Error compiling dynamic rules: " + kieBuilder.getResults().toString());
        }
        KieBase tempKieBase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        RuleUnitExecutor ruleUnitExecutor = RuleUnitExecutor.create().bind(tempKieBase);
        PersonRuleUnits unit = new PersonRuleUnits(this);

        try {
            ruleUnitExecutor.run(unit);
            unit.getPersons().append(dto);
        }finally {
            ruleUnitExecutor.dispose();
        }
    }
}
