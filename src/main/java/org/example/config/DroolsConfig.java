package org.example.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class DroolsConfig {

    private final Logger log= LoggerFactory.getLogger(DroolsConfig.class);
    private final KieServices kieServices = KieServices.Factory.get();

    // In-memory storage for our DRL rules. Key: ruleId/ruleName, Value: DRL content
    private final Map<String, String> activeRules = new ConcurrentHashMap<>();

    // A unique ReleaseId for our dynamically built KieModule.
    // We'll increment the snapshot version to force updates.
    private final String GROUP_ID = "com.example.carrental";
    private final String ARTIFACT_ID = "dynamic-rules";
    private final AtomicLong VERSION_COUNTER = new AtomicLong(1); // For dynamic versioning

    public DroolsConfig() throws IOException {
        loadInitialRules();
    }

    private void loadInitialRules() throws IOException {
        String[] initialRules = {
                "rules/person-eligibility.drl",
                "rules/car-eligibility.drl",
                "rules/rental-contract.drl"
        };

        for (String initialRule : initialRules) {
            try (Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(initialRule)))) {
                StringBuilder rulesContent = new StringBuilder();
                char[] buffer = new char[1024];
                int byteCountRead;
                while ((byteCountRead = reader.read(buffer)) != -1) {
                    rulesContent.append(buffer, 0, byteCountRead);
                }
                activeRules.put(initialRule, rulesContent.toString());
                System.out.println("Loaded initial rule: " + initialRule);
            } catch (IOException e) {
                System.err.println("Failed to load initial rule from classpath: " + initialRule + " - " + e.getMessage());
                throw e; // Re-throw to fail application startup if initial rules are missing
            }
        }
    }

    // This bean will be updated whenever rules are added/updated/deleted
    private volatile KieContainer kieContainer;

    @Bean
    public KieContainer kieContainer() {
        // Build the initial KieContainer
        this.kieContainer = buildKieContainer();
        return this.kieContainer;
    }

    // Method to dynamically build and refresh the KieContainer
    public synchronized KieContainer buildKieContainer() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        //generate a new releasedId to force Drools to recognize update
        String newVersion = VERSION_COUNTER.getAndIncrement() + ".0-SNAPSHOT";
        ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, newVersion);
        // Generate a minimal kmodule.xml programmatically
        // This ensures the KieContainer knows about your KieBase and KieSession setup
        //kbase name="CarRentalKBase": Defines a knowledge base named "CarRentalKBase".-->
        //packages="rules": Tells Drools to look for DRL files within the rules package (i.e., src/main/resources/rules/).-->
        //default="true": Marks this KieBase as the default, so it can be easily retrieved.-->
        //ksession name="CarRentalKSession": Defines a knowledge session named "CarRentalKSession".-->
        //type="stateless": Specifies that this session is stateless (meaning facts are inserted, rules fired, and then the session is disposed for each interaction).-->
        String kModuleXml = """
                 <kmodule xmlns="http://www.drools.org/xsd/kmodule">
                    <kbase name="CarRentalKBase" packages="rules" default="true">
                        <ksession name="CarRentalKSession" default="true" type="stateless"/>
                    </kbase>
                </kmodule>
                """;
        String KMODULE_XML = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kmodule xmlns="http://www.drools.org/xsd/kmodule">
                    <kbase name="CarRentalKBase" packages="rules" default="true">
                        <ksession name="CarRentalKSession" default="true" type="stateless"/>
                    </kbase>
                </kmodule>
                """;

        kieFileSystem.write("src/main/resources/META-INF/kmodule.xml", kieServices.getResources().newReaderResource(new java.io.StringReader(kModuleXml)));
        kieFileSystem.generateAndWritePomXML(releaseId); // Generate a pom.xml for the in-memory kjar

        activeRules.forEach((rulePath, drlContent) -> {
            kieFileSystem.write("src/main/resources/" + rulePath, // Simulating standard resource paths
                    kieServices.getResources().newReaderResource(new java.io.StringReader(drlContent))
                            .setResourceType(ResourceType.DRL));
            log.info("Writing rule to KieFileSystem: {}", rulePath);
        });

        // Build the KieModule
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            // Log and throw if there are compilation errors in DRL
            StringBuilder errors = new StringBuilder("Drools rule compilation errors:\n");
            for (Message message : kieBuilder.getResults().getMessages(Message.Level.ERROR)) {
                errors.append(message.toString()).append("\n");
            }
            System.err.println(errors.toString());
            throw new RuntimeException("Drools rule compilation failed!");
        }

        KieModule kieModule= kieBuilder.getKieModule();
        // Add the new KieModule to the KieRepository. This makes it available for KieContainer.
        // If a module with the same ReleaseId exists, it will be replaced.
        kieServices.getRepository().addKieModule(kieModule);

        // Create or update the KieContainer.
        // If kieContainer is null or needs to be refreshed, create a new one.
        // kieContainer.updateToVersion(releaseId) would also work, but creating a new one
        // and re-injecting through Spring context (if possible/needed) ensures fresh state.
        if (this.kieContainer != null) {
            this.kieContainer.dispose(); // Dispose old container if exists
        }
        this.kieContainer = kieServices.newKieContainer(releaseId);
        log.info("KieContainer updated to version: {}", releaseId);
        return this.kieContainer;

    }

    /**
     * Adds a new rule or updates an existing rule.
     * @param ruleName A unique identifier for the rule (e.g., "my-custom-discount-rule").
     * @param drlContent The full DRL content of the rule, including package and imports.
     */
    public void addOrUpdateRule(String ruleName, String drlContent) {
        // We use a convention for the rule's path within the KieFileSystem
        // The ruleName will be part of the DRL file path, allowing specific updates.
        String rulePath = "rules/dynamic/" + ruleName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".drl";
        activeRules.put(rulePath, drlContent);
        log.info("Rule '{}' added/updated.", ruleName);
        buildKieContainer(); // Rebuild and update the container immediately
    }

    /**
     * Deletes a rule by its name.
     * @param ruleName The unique identifier of the rule to delete.
     */
    public void deleteRule(String ruleName) {
        String rulePath = "rules/dynamic/" + ruleName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".drl";
        if (activeRules.remove(rulePath) != null) {
            log.info("Rule '{}' deleted.", ruleName);
            buildKieContainer(); // Rebuild and update the container immediately
        } else {
            log.info("Rule '{}' not found for deletion.", ruleName);
        }
    }

    // Expose active rules for debugging/listing
    public Map<String, String> getActiveRules() {
        return new HashMap<>(activeRules);
    }
}


