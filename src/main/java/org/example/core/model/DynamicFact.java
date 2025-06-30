package org.example.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;


public class DynamicFact {
    @JsonIgnore
    private List<String> factType;
    @JsonIgnore
    private String factId;
    @JsonIgnore
    private Map<String, Object> attributes;
    @JsonIgnore
    private Map<String, Object> results;
    @JsonIgnore
    private boolean processed;
    @JsonIgnore
    private Map<String, Object> fieldMap;

    @JsonIgnore
    private Map<RuleDefinition,Boolean> rulesStatusMap;

    @JsonIgnore
    private Map<String,List<String>> errorMap;
    
    public DynamicFact() {
        this.attributes = new HashMap<>();
        this.results = new HashMap<>();
        this.processed = false;
        this.rulesStatusMap = new HashMap<>();
        this.fieldMap = new HashMap<>();
        this.errorMap = new HashMap<>();
    }

    public List<String> getWrapParameters(List<String> params) {
        return List.of();
    }
    
    public DynamicFact(List<String> factType, Map<String, Object> attributes) {
        this();
        this.factType = new HashSet<>(factType).stream().toList();
        this.attributes = attributes;
        this.factId = UUID.randomUUID().toString();
    }

    public void addRuleStatus(RuleDefinition ruleDefinition, boolean status) {
        if (rulesStatusMap.containsKey(ruleDefinition)) return;
        rulesStatusMap.put(ruleDefinition,status);
    }

    public void addFieldMap(Map<String, Object> fieldMap) {
        this.fieldMap.putAll(fieldMap);
    }

    public void addFieldValue(String fieldName, Object fieldValue) {
        this.fieldMap.put(fieldName, fieldValue);
    }

    public Object getFieldValue(String fieldName) {
        return this.fieldMap.get(fieldName);
    }

    public Map<RuleDefinition, Boolean> getRulesStatusMap() {
        return rulesStatusMap;
    }

    public void addErrorItem(String errorCode, List<String> params) {
        errorMap.put(errorCode, params);
    }

    public Map<String, List<String>> getErrorMap() {
        return errorMap;
    }

    public void setRulesStatusMap(Map<RuleDefinition, Boolean> rulesStatusMap) {
        this.rulesStatusMap = rulesStatusMap;
    }

    // Generic getter for any attribute
    public Object get(String key) {
        return attributes.get(key);
    }
    
    // Generic setter for any attribute
    public void set(String key, Object value) {
        attributes.put(key, value);
    }
    
    // Type-safe getters
    public String getString(String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getInt(String key) {
        Object value = attributes.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
    
    public Double getDouble(String key) {
        Object value = attributes.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
    
    public Boolean getBoolean(String key) {
        Object value = attributes.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getList(String key) {
        Object value = attributes.get(key);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String key) {
        Object value = attributes.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new HashMap<>();
    }

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }

    // Check if attribute exists
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    // Result management
    public void setResult(String key, Object value) {
        results.put(key, value);
    }
    
    public Object getResult(String key) {
        return results.get(key);
    }
    
    // Getters and setters
    public List<String> getFactType() { return factType; }
    public void setFactType(List<String> factType) {
        if (this.factType == null) this.factType = new ArrayList<>();
        if (factType != null && !new HashSet<>(this.factType).containsAll(factType)) {
            this.factType.addAll(factType);
        }
    }
    
    public String getFactId() { return factId; }
    public void setFactId(String factId) { this.factId = factId; }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results = results; }
    
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
    
    @Override
    public String toString() {
        return "DynamicFact{" +
                "factType='" + factType + '\'' +
                ", factId='" + factId + '\'' +
                ", attributes=" + attributes +
                ", results=" + results +
                '}';
    }
}