package org.example.controller;

import org.example.dto.RuleDto;
import org.example.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    private String ruleContent=  "package %s;\n" +
            "\n" +
            "\n" +
            "unit PersonRuleUnit;\n" +
            "\n" +
            "rule \"will execute per each Person having name tmp\"\n" +
            "when\n" +
            "    $m: /persons[ name == \"hasan\" ]\n" +
            "then\n" +
            "    System.out.println(\"hello\");\n" +
            "end\n";
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addRule(@RequestParam String ruleType,
                        @RequestParam String ruleName,
                        @RequestParam MultipartFile ruleFile) throws IOException {
        RuleDto rule = new RuleDto();
        rule.setRuleType(ruleType);
        rule.setRuleName(ruleName);
        rule.setRuleContent(new String(ruleFile.getBytes()));
        ruleService.createRule(rule);
    }


}
