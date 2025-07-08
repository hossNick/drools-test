package org.example.service;

import org.example.config.KieContainerFactory;
import org.example.entities.Contract;
import org.example.repositories.CarRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

//@Service
public class ContractService {

    private final Logger log = LoggerFactory.getLogger(ContractService.class);
    private final KieContainerFactory kieContainer;
    private final CarRepository carRepository; // Inject the mock repository


    public ContractService(KieContainerFactory kieContainer, CarRepository carRepository) {
        this.kieContainer = kieContainer;
        this.carRepository = carRepository;
    }




    public Contract evaluateRentalContract(Contract contract) throws IOException {
        // Create a new KieSession from the KieContainer.
        // Each KieSession is a runtime instance of the rule engine.
        // For stateless sessions, it's good practice to create a new one per request
        // and dispose of it afterward to avoid state contamination.
        KieSession kieSession = kieContainer.getKieContainerForEntity("contract").newKieSession("CarRentalKSession"); // Use the named session from kmodule.xml

        try {
            // Set the global variable.
            // This makes the Spring-managed CarRepository instance available to your DRL rules.
            // The DRL rule can then call methods on this 'carRepository' object.
            kieSession.setGlobal("carRepository", carRepository);

            // Insert facts into the working memory of the KieSession.
            // Drools will then evaluate these facts against the loaded rules.
            kieSession.insert(contract.getOwner()); // Insert person fact
            kieSession.insert(contract.getCar());   // Insert car fact
            kieSession.insert(contract);            // Insert contract fact

            // Fire all rules that match the inserted facts.
            int rulesFired = kieSession.fireAllRules();
            log.info("Rules fired for contract {}: {}", contract.getId(), rulesFired);

        } finally {
            // Dispose the KieSession to release resources.
            // This is crucial, especially for stateless sessions, to prevent memory leaks.
            kieSession.dispose();
        }
        return contract;
    }

}
