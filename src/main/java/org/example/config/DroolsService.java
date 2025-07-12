package org.example.config;

import org.example.entities.Rule;
import org.example.service.RuleService;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DroolsService {

    private final Logger log = LoggerFactory.getLogger(DroolsService.class);
    private final RuleService ruleService;
    private final String GROUP_ID = "com.example.riton";
    private final String ARTIFACT_ID = "dynamic-rules";
    private final AtomicLong VERSION_COUNTER = new AtomicLong(1);
    private static final String DEFAULT_PACKAGE = "org.example";

    private static String generateKModuleBy(String entityName) {
        return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <kmodule xmlns="http://www.drools.org/xsd/kmodule">
                    <kbase name="validate" packages="%s.*" default="true">
                        <ksession name="%s" default="true" type="stateless"/>
                    </kbase>
                </kmodule>
                """,DEFAULT_PACKAGE, entityName);
    }

    public DroolsService(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    public void validateByEntityName(BaseDto dto) {
        log.info("validateByEntityName " + dto.getClass().getSimpleName());
        List<Rule> rules = ruleService.getRuleByType(dto.getClass().getSimpleName());
        KieContainer kieContainer = createTempContainer(rules, dto.getClass().getSimpleName());
        StatelessKieSession kieSession = kieContainer.newStatelessKieSession(dto.getClass().getSimpleName());
        kieSession.execute(dto);

    }

    public void validateByEntityName(List<BaseDto> dto) {
        if (dto.isEmpty()) return;
        log.info("validateByEntityName " + dto.getFirst().getClass().getSimpleName());
        List<Rule> rules = ruleService.getRuleByType(dto.getFirst().getClass().getSimpleName());
        KieContainer kieContainer = createTempContainer(rules, dto.getFirst().getClass().getSimpleName());
        List<Command<?>> cmds = new ArrayList<>();
        dto.forEach(item-> cmds.add(CommandFactory.newInsert(item, "PersonDto",true, "MyEntryPoint")));
        StatelessKieSession kieSession = kieContainer.newStatelessKieSession(dto.getFirst().getClass().getSimpleName());
        kieSession.execute(CommandFactory.newBatchExecution(cmds));
//        kieSession.execute(dto);

    }

    public KieContainer createTempContainer(List<Rule> rules, String entityName) {

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/META-INF/kmodule.xml",
                ks.getResources().newReaderResource(new java.io.StringReader(generateKModuleBy(entityName))));
        String newVersion = VERSION_COUNTER.getAndIncrement() + ".0-SNAPSHOT";
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, newVersion);
        kfs.generateAndWritePomXML(releaseId); // Generate a pom.xml for the in-memory kjar
        for (Rule r : rules) {
            log.info("Rule '{}'", r.getRuleContent());
            String sb = "src/main/resources/" + DEFAULT_PACKAGE + "/" + r.getRuleName() + ".drl";
            kfs.write(sb, ks.getResources().newReaderResource(new StringReader(r.getRuleContent()))
                    .setResourceType(ResourceType.DRL));
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Error compiling dynamic rules: " + kieBuilder.getResults().toString());
        }
        KieModule km = kieBuilder.getKieModule();
        ks.getRepository().addKieModule(km);
        return ks.newKieContainer(releaseId);
    }
}




