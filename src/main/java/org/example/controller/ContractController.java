package org.example.controller;

import org.example.config.DroolsConfig;
import org.example.entities.Car;
import org.example.entities.Contract;
import org.example.entities.Person;
import org.example.repositories.CarRepository;
import org.example.service.ContractService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rental")
public class ContractController {

    private final Logger log = LoggerFactory.getLogger(ContractController.class);
    private final DroolsConfig droolsConfig;
    private final ContractService rentalService;
    private final CarRepository carRepository; // For getting car details
    private final KieContainer kieContainer; // To access KieScanner
    private final KieServices kieServices; // To get KieScanner instance

    public ContractController(DroolsConfig droolsConfig, ContractService rentalService
            , CarRepository carRepository, KieContainer kieContainer, KieServices kieServices) {
        this.droolsConfig = droolsConfig;
        this.rentalService = rentalService;
        this.carRepository = carRepository;
        this.kieContainer = kieContainer;
        this.kieServices = kieServices;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Contract> evaluateContract(@RequestBody Contract contract) {
        log.info("Received contract for evaluation: {}", contract.getId());
        Contract evaluatedContract = rentalService.evaluateRentalContract(contract);
        return ResponseEntity.ok(evaluatedContract);
    }

    @GetMapping("/test-contract")
    public ResponseEntity<Contract> createAndEvaluateTestContract(@RequestBody Contract contract) {
         Contract evaluatedContract = rentalService.evaluateRentalContract(contract);
        return ResponseEntity.ok(evaluatedContract);
    }

    @PostMapping("/trigger-rule-scan")
    public ResponseEntity<String> triggerRuleScan() {
        // Get the KieScanner instance associated with the KieContainer.
        // This allows you to manually trigger a scan for rule updates.
        // In a production environment, KieScanner's polling interval handles this automatically,
        // but this endpoint is useful for immediate testing or specific deployment scenarios.
        KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
        kieScanner.scanNow(); // Force a synchronous scan for new kjar versions
        log.info("Manually triggered KieScanner scan.");
        return ResponseEntity.ok("KieScanner scan triggered. Check logs for updates.");
    }
    @PostMapping("/rules")
    public ResponseEntity<String> addOrUpdateRule(@RequestParam String ruleName, @RequestBody String drlContent) {
        try {
            droolsConfig.addOrUpdateRule(ruleName, drlContent);
            return ResponseEntity.ok("Rule '" + ruleName + "' added/updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to add/update rule: " + e.getMessage());
        }
    }

    @DeleteMapping("/rules")
    public ResponseEntity<String> deleteRule(@RequestParam String ruleName) {
        try {
            droolsConfig.deleteRule(ruleName);
            return ResponseEntity.ok("Rule '" + ruleName + "' deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to delete rule: " + e.getMessage());
        }
    }

    @GetMapping("/rules")
    public ResponseEntity<Map<String, String>> getAllRules() {
        return ResponseEntity.ok(droolsConfig.getActiveRules());
    }

    // This endpoint replaces trigger-rule-scan and forces a rebuild
    @PostMapping("/rules/rebuild-container")
    public ResponseEntity<String> rebuildKieContainer() {
        try {
            droolsConfig.buildKieContainer();
            return ResponseEntity.ok("KieContainer manually rebuilt successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Failed to rebuild KieContainer: " + e.getMessage());
        }
    }
}
