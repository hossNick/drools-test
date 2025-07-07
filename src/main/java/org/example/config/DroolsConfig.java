package org.example.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class DroolsConfig {

//    @Bean
//    public KieContainer kieContainer() {
//        return KieServices.get().newKieClasspathContainer();
//    }
//
//    @Bean
//    public RuleUnitExecutor ruleUnitExecutor(KieContainer kieContainer) {
//        return RuleUnitExecutor.create().bind(kieContainer.getKieBase());
//    }

    private final String KMODULE_XML = """
        <?xml version="1.0" encoding="UTF-8"?>
        <kmodule xmlns="http://www.drools.org/xsd/kmodule">
            <kbase name="CarRentalKBase" packages="*">
                <ksession name="CarRentalKSession"/>
            </kbase>
        </kmodule>
        """;
    @Bean
    public KieContainer kieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        // Write kmodule.xml to the KieFileSystem
        kfs.write("src/main/resources/META-INF/kmodule.xml", KMODULE_XML);

        // Load your static DRL files from resources/rules/
        loadInitialRulesFromClasspath(kfs);

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll(); // This compiles all DRL and the kmodule.xml

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        return ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
    }

    private void loadInitialRulesFromClasspath(KieFileSystem kfs) {
        String[] initialRuleFiles = {
                "rules/person-validation-unit-rules.drl", // Example existing rules
//                "rules/car-eligibility.drl",
//                "rules/rental-contract.drl",
                // Add the RuleUnit-specific DRL. It needs to be compiled into the main KieContainer
                // if you intend to use the RuleUnit from this KieContainer.
//                "rules/person-validation-unit-rules.drl"
        };

        for (String ruleFile : initialRuleFiles) {
            try {
                org.springframework.core.io.Resource resource = new ClassPathResource(ruleFile);
                try (InputStream is = resource.getInputStream()) {
                    String drlContent = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
                    kfs.write("src/main/resources/" + ruleFile, drlContent);
                    System.out.println("Loaded DRL: " + ruleFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load DRL file: " + ruleFile, e);
            }
        }
    }
}



