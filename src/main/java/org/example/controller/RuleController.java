package org.example.controller;

import org.example.dto.RuleDto;
import org.example.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    public void addRule(@RequestBody RuleDto rule) {
        rule.setRuleContent(ruleContent);
        ruleService.createRule(rule);
    }

}
