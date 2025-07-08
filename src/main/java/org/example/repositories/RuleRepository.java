package org.example.repositories;

import org.example.entities.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RuleRepository extends JpaRepository<Rule, UUID> {
    @Query("select r from Rule r where r.ruleType = ?1")
    List<Rule> findByRuleType(String ruleType);
}