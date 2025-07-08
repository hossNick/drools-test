package org.example.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//@Configuration
public class KieContainerFactory {

    private final Logger log = LoggerFactory.getLogger(KieContainerFactory.class);
    private final String GROUP_ID = "com.example.carrental";
    private final String ARTIFACT_ID = "dynamic-rules";
    private final AtomicLong VERSION_COUNTER = new AtomicLong(1);
    protected final String prePath = "rules/";
    private final Map<String, KieContainer> kieContainers = new ConcurrentHashMap<>();
    private final KieServices kieServices = KieServices.Factory.get();
    private final Map<String, String> activeRules = new ConcurrentHashMap<>();

    public KieContainer getKieContainerForEntity(String entityName) throws IOException {
        return kieContainers.computeIfAbsent(entityName, this::createKieContainer);
    }

//    @Bean
    public KieServices kieService() {
        return  KieServices.Factory.get();
    }

    private KieContainer createKieContainer(String entityName) {
        initRuleFile(entityName);
        KieFileSystem kieFileSystem = kieService().newKieFileSystem();

        String newVersion = VERSION_COUNTER.getAndIncrement() + ".0-SNAPSHOT";
        ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, newVersion);



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
        kieServices.getRepository().addKieModule(kieModule);
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    protected void initRuleFile(String entityName) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String rulesPath = prePath + entityName.toLowerCase() + "/*";
        Resource[] resources;
        try {
            resources = resolver.getResources(rulesPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] rulesFileName = Arrays.stream(resources).map(Resource::getFilename)
                .toArray(String[]::new);

        for (String initialRule : rulesFileName) {
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
                throw new RuntimeException(e); // Re-throw to fail application startup if initial rules are missing
            }
        }

    }

    /**
     * Adds a new rule or updates an existing rule.
     * @param ruleName A unique identifier for the rule (e.g., "my-custom-discount-rule").
     * @param drlContent The full DRL content of the rule, including package and imports.
     */
    public void addOrUpdateRule(String ruleName, String drlContent,String entityName) throws IOException {
        // We use a convention for the rule's path within the KieFileSystem
        // The ruleName will be part of the DRL file path, allowing specific updates.
        String rulePath = "rules/dynamic/" + ruleName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".drl";
        activeRules.put(rulePath, drlContent);
        log.info("Rule '{}' added/updated.", ruleName);
        getKieContainerForEntity(entityName); // Rebuild and update the container immediately
    }

    /**
     * Deletes a rule by its name.
     * @param ruleName The unique identifier of the rule to delete.
     */
    public void deleteRule(String ruleName, String entityName) throws IOException {
        String rulePath = "rules/dynamic/" + ruleName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".drl";
        if (activeRules.remove(rulePath) != null) {
            log.info("Rule '{}' deleted.", ruleName);
            getKieContainerForEntity(entityName); // Rebuild and update the container immediately
        } else {
            log.info("Rule '{}' not found for deletion.", ruleName);
        }
    }

    // Expose active rules for debugging/listing
    public Map<String, String> getActiveRules() {
        return new HashMap<>(activeRules);
    }
}
