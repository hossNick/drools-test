package org.example.core.model;

import java.util.HashMap;
import java.util.Map;


public class RuleAction {
    
    public enum ActionType {
        SET_VALUE,
        CALCULATE,
        ADD_TO_LIST,
        REMOVE_FROM_LIST,
        INCREMENT,
        DECREMENT,
        MULTIPLY,
        DIVIDE,
        CONCATENATE,
        CUSTOM
    }
    
    private ActionType type;
    private String targetField;
    private Object value;
    private String sourceField;
    private Map<String, Object> parameters;
    
    public RuleAction() {
        this.parameters = new HashMap<>();
    }
    
    public RuleAction(ActionType type, String targetField, Object value) {
        this();
        this.type = type;
        this.targetField = targetField;
        this.value = value;
    }
    
    public void execute(DynamicFact fact) {
        switch (type) {
            case SET_VALUE:
                fact.setResult(targetField, value);
                break;
                
            case CALCULATE:
                executeCalculation(fact);
                break;
                
            case ADD_TO_LIST:
                addToList(fact);
                break;
                
            case REMOVE_FROM_LIST:
                removeFromList(fact);
                break;
                
            case INCREMENT:
                increment(fact);
                break;
                
            case DECREMENT:
                decrement(fact);
                break;
                
            case MULTIPLY:
                multiply(fact);
                break;
                
            case DIVIDE:
                divide(fact);
                break;
                
            case CONCATENATE:
                concatenate(fact);
                break;
                
            case CUSTOM:
                executeCustom(fact);
                break;
        }
    }
    
    private void executeCalculation(DynamicFact fact) {
        if (parameters.containsKey("formula")) {
            String formula = parameters.get("formula").toString();
            // Simple formula evaluation (can be enhanced with expression evaluator)
            Object result = evaluateFormula(formula, fact);
            fact.setResult(targetField, result);
        }
    }
    
    private Object evaluateFormula(String formula, DynamicFact fact) {
        // Simple implementation - can be enhanced with proper expression evaluator
        // For now, just handle basic operations
        String processed = formula;
        
        // Replace field references with values
        for (String key : fact.getAttributes().keySet()) {
            Object value = fact.get(key);
            if (value instanceof Number) {
                processed = processed.replace("{" + key + "}", value.toString());
            }
        }
        
        // Basic evaluation (in production, use proper expression evaluator)
        try {
            if (processed.contains("*")) {
                String[] parts = processed.split("\\*");
                double result = Double.parseDouble(parts[0].trim());
                for (int i = 1; i < parts.length; i++) {
                    result *= Double.parseDouble(parts[i].trim());
                }
                return result;
            }
            return Double.parseDouble(processed);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void addToList(DynamicFact fact) {
        Object currentValue = fact.getResult(targetField);
        if (currentValue instanceof java.util.List) {
            ((java.util.List<Object>) currentValue).add(value);
        } else {
            java.util.List<Object> newList = new java.util.ArrayList<>();
            newList.add(value);
            fact.setResult(targetField, newList);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void removeFromList(DynamicFact fact) {
        Object currentValue = fact.getResult(targetField);
        if (currentValue instanceof java.util.List) {
            ((java.util.List<Object>) currentValue).remove(value);
        }
    }
    
    private void increment(DynamicFact fact) {
        Object current = fact.get(sourceField != null ? sourceField : targetField);
        if (current instanceof Number) {
            double val = ((Number) current).doubleValue();
            double incrementBy = value instanceof Number ? ((Number) value).doubleValue() : 1.0;
            fact.setResult(targetField, val + incrementBy);
        }
    }
    
    private void decrement(DynamicFact fact) {
        Object current = fact.get(sourceField != null ? sourceField : targetField);
        if (current instanceof Number) {
            double val = ((Number) current).doubleValue();
            double decrementBy = value instanceof Number ? ((Number) value).doubleValue() : 1.0;
            fact.setResult(targetField, val - decrementBy);
        }
    }
    
    private void multiply(DynamicFact fact) {
        Object current = fact.get(sourceField != null ? sourceField : targetField);
        if (current instanceof Number && value instanceof Number) {
            double val = ((Number) current).doubleValue();
            double multiplyBy = ((Number) value).doubleValue();
            fact.setResult(targetField, val * multiplyBy);
        }
    }
    
    private void divide(DynamicFact fact) {
        Object current = fact.get(sourceField != null ? sourceField : targetField);
        if (current instanceof Number && value instanceof Number) {
            double val = ((Number) current).doubleValue();
            double divideBy = ((Number) value).doubleValue();
            if (divideBy != 0) {
                fact.setResult(targetField, val / divideBy);
            }
        }
    }
    
    private void concatenate(DynamicFact fact) {
        String str1 = fact.get(sourceField != null ? sourceField : targetField).toString();
        String str2 = value.toString();
        fact.setResult(targetField, str1 + str2);
    }
    
    private void executeCustom(DynamicFact fact) {
        // Hook for custom action implementations
        // Can be extended based on specific requirements
    }
    
    // Getters and setters
    public ActionType getType() { return type; }
    public void setType(ActionType type) { this.type = type; }
    
    public String getTargetField() { return targetField; }
    public void setTargetField(String targetField) { this.targetField = targetField; }
    
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    
    public String getSourceField() { return sourceField; }
    public void setSourceField(String sourceField) { this.sourceField = sourceField; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}