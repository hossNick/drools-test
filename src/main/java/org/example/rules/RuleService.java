package org.example.rules;

import org.example.entities.Rule;
import org.example.repositories.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Rule findById(UUID id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    public List<Rule> findByType(String type) {
        return ruleRepository.findByRuleType(type);
    }

    public Rule update(UUID id, Rule rule) {
        Rule ruleToUpdate = findById(id);
        ruleToUpdate.setRuleContent(rule.getRuleContent());
        ruleToUpdate.setRuleType(rule.getRuleType());
        ruleToUpdate.setRuleName(rule.getRuleName());
        return ruleRepository.save(ruleToUpdate);
    }
}
