package org.example.core.config;


import jakarta.annotation.Nullable;
import org.example.core.common.util.SpringUtil;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DroolsConfig {

    private static final String RULES_PATH = "rules/";

    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    public static KieContainer getKieContainer(List<String> fileNames) throws IOException {
        return SpringUtil.getBean(DroolsConfig.class).loadKieContainerForFiles(fileNames);
    }


    public KieContainer loadKieContainerForFiles(List<String> fileNamesExtra) throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        ArrayList<String> fileNames = new ArrayList<>(fileNamesExtra);
        fileNames.add("dynamic-rules.drl");
        for (String fileName : fileNames) {
            String fullPath = "classpath*:" + RULES_PATH + fileName;
            Resource[] resources = resolver.getResources(fullPath);

            if (resources.length == 0) {
                throw new IOException("Rule file not found: " + fileName);
            }

            Resource resource = resources[0];
            String content = new String(resource.getInputStream().readAllBytes());

            log.info("Loading rule file: {}", fileName);
            kfs.write("src/main/resources/" + RULES_PATH + fileName, content);
        }

        writeKModuleXML(kfs);

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        Results results = kieBuilder.buildAll().getResults();

        if (results.hasMessages(Message.Level.ERROR)) {
            for (Message message : results.getMessages(Message.Level.ERROR)) {
                log.error("Rule error: {}", message);
            }
            throw new RuntimeException("Rule compilation failed");
        }

        return ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }


    @Bean
    public KieContainer kieContainer(KieServices kieServices) throws IOException {
        return loadKieContainer(kieServices,"classpath*:" + RULES_PATH + "**/*.drl");
    }

    private KieContainer loadKieContainer(@Nullable KieServices kieServices, String drlPath) throws IOException {
        KieFileSystem kieFileSystem = kieServices == null ? KieServices.Factory.get().newKieFileSystem() : kieServices.newKieFileSystem();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();


        Resource[] resources = resourcePatternResolver.getResources(drlPath);

        log.info("Found {} rule files", resources.length);

        for (Resource resource : resources) {
            String path = RULES_PATH + resource.getFilename();
            log.info("Loading rule file: {}", path);

            try {
                String content = new String(resource.getInputStream().readAllBytes());
                kieFileSystem.write("src/main/resources/" + path, content);
            } catch (Exception e) {
                log.error("Error loading rule file: {}", path, e);
            }
        }

        writeKModuleXML(kieFileSystem);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        Results results = kieBuilder.buildAll().getResults();

        if (results.hasMessages(Message.Level.ERROR)) {
            log.error("Rules compilation errors:");
            for (Message message : results.getMessages(Message.Level.ERROR)) {
                log.error("Error: {}", message.getText());
            }
            throw new RuntimeException("Rules compilation failed");
        }

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    private void writeKModuleXML(KieFileSystem kieFileSystem) {
        String kmoduleXML = """
        <?xml version="1.0" encoding="UTF-8"?>
        <kmodule xmlns="http://www.drools.org/xsd/kmodule">
            <kbase name="rules" packages="com.insurance.rules" default="true">
                <ksession name="dynamic-rules-session" default="true"/>
            </kbase>
        </kmodule>
        """;
        kieFileSystem.writeKModuleXML(kmoduleXML);
    }

}