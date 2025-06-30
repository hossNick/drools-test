package org.example.core.model;



import org.example.core.common.BaseValidationCode;
import org.example.core.common.CommonBaseValidationCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RuleDefinition{
    private String ruleId;
    private String ruleName;
    private BaseValidationCode descriptionCode;
    private List<String> parameters;
    private String factType;
    private int priority;
    private boolean enabled;
    private Class clazz;
    private List<RuleCondition> conditions;
    private List<RuleAction> actions;

    public RuleDefinition() {
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.enabled = true;
        this.priority = 0;
        this.parameters = new ArrayList<>();
    }
    
    public RuleDefinition(String ruleId, String ruleName, String factType) {
        this();
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.factType = factType;
    }

    public RuleDefinition(String ruleId, String ruleName) {
        this();
        this.ruleId = ruleId;
        this.ruleName = ruleName;
    }
    
    // Builder pattern for fluent API
    public static class Builder {
        private RuleDefinition rule;
        
        public Builder(String ruleId, String ruleName, String factType) {
            rule = new RuleDefinition(ruleId, ruleName, factType);
        }

        public Builder(String ruleId, String ruleName, String factType,Class clazz) {
            rule = new RuleDefinition(ruleId, ruleName, factType);
            rule.setClazz(clazz);
        }
        
        public Builder descriptionCode(BaseValidationCode descriptionCode) {
            rule.setDescription(descriptionCode);
            return this;
        }

        public Builder addParam(String param) {
            rule.getParameters().add(param);
            return this;
        }
        
        public Builder priority(int priority) {
            rule.setPriority(priority);
            return this;
        }
        
        public Builder when(RuleCondition condition) {
            rule.addCondition(condition);
            return this;
        }

        public Builder whenRequireNotNull(String field) {
            descriptionCode(CommonBaseValidationCode.ERR_10000_1001);
            rule.addCondition(RuleCondition.requiredNotNullRule(field));
            return this;
        }

        public Builder whenRequireMin(String field,int min) {
            descriptionCode(CommonBaseValidationCode.ERR_10000_1010);
            addParam(String.valueOf(min));
            rule.addCondition(RuleCondition.requiredMinRule(field,min));
            return this;
        }
        
        public Builder then(RuleAction action) {
            rule.addAction(action);
            return this;
        }
        
        public RuleDefinition build() {
//            rule.getDescription().setParameters(rule.getParameters());
            return rule;
        }
    }
    
    public void addCondition(RuleCondition condition) {
        conditions.add(condition);
    }
    
    public void addAction(RuleAction action) {
        actions.add(action);
    }
    
    public boolean evaluate(DynamicFact fact) {
        if (!enabled || !fact.getFactType().contains(factType)) {
            return false;
        }
        
        // All conditions must be true (AND logic by default)
        boolean result = conditions.stream().allMatch(condition -> condition.evaluate(fact));
        fact.addRuleStatus(this, result);
        return result;
    }

    public void execute(DynamicFact fact) {
        actions.forEach(action -> action.execute(fact));
        fact.setProcessed(true);
    }

    public List<String> getWrapParameters(DynamicFact fact) {
        return fact.getWrapParameters(getParameters());
    }
    // Getters and setters

    public List<String> getParameters() {
        if (parameters == null) return new ArrayList<>();
        return parameters;
    }

    public String getDrlFormatParamList() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getParameters().size(); i++) {
            result.append("\"").append(getParameters().get(i)).append("\"");
            if (i != getParameters().size() - 1)
                result.append(",");
        }

        return result.toString();
    }

    public void setParameters(List<String> parameters) {
        if (parameters == null) return;
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RuleDefinition) {
            RuleDefinition other = (RuleDefinition) obj;
            return other.ruleId.equals(this.ruleId) && other.ruleName.equals(this.ruleName);
        }
        return super.equals(obj);
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, ruleName);
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public BaseValidationCode getDescription() { return descriptionCode; }
    public void setDescription(BaseValidationCode descriptionCode) { this.descriptionCode = descriptionCode; }
    
    public String getFactType() { return factType; }
    public void setFactType(String factType) { this.factType = factType; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<RuleCondition> getConditions() { return conditions; }
    public void setConditions(List<RuleCondition> conditions) { this.conditions = conditions; }
    
    public List<RuleAction> getActions() { return actions; }
    public void setActions(List<RuleAction> actions) { this.actions = actions; }
}