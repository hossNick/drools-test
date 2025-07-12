package org.example.service;

import org.example.config.DroolsService;
import org.example.dto.RuleDto;
import org.example.entities.Rule;
import org.example.repositories.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    @Autowired
    private DroolsService droolsService;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
//        this.droolsService = droolsService;
    }

    public List<Rule> getRuleByType(String type) {
        return ruleRepository.findByRuleType(type);
    }

    public Rule createRule(RuleDto ruleDto) {
        Rule rule = new Rule();
        rule.setRuleType(ruleDto.getRuleType());
        rule.setRuleContent(ruleDto.getRuleContent());
        rule.setRuleName(ruleDto.getRuleName());
//        droolsService.createTempContainer(List.of(rule), ruleDto.getRuleType());
        return ruleRepository.save(rule);
    }
}
