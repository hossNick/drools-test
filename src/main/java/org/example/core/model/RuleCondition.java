package org.example.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



public class RuleCondition {

    public enum ConditionType {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        CONTAINS,
        NOT_CONTAINS,
        REGEX,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL,
        BETWEEN,
        CUSTOM
    }

    public enum LogicalOperator {
        AND, OR, NOT
    }

    private String field;
    private ConditionType type;
    private Object value;
    private Object value2; // For BETWEEN condition
    private List<RuleCondition> nestedConditions;
    private LogicalOperator operator;


    public static RuleCondition requiredNotNullRule(String field) {
        return new RuleCondition(field, ConditionType.IS_NOT_NULL, null);
    }

    public static RuleCondition requiredMinRule(String field, int min) {
        return new RuleCondition(field, ConditionType.GREATER_THAN, min);
    }

    public RuleCondition(String field, ConditionType type, Object value) {
        this.field = field;
        this.type = type;
        this.value = value;
    }

    public RuleCondition(LogicalOperator operator, List<RuleCondition> nestedConditions) {
        this.operator = operator;
        this.nestedConditions = nestedConditions;
    }

    public boolean evaluate(DynamicFact fact) {
        // Handle nested conditions
        if (nestedConditions != null && !nestedConditions.isEmpty()) {
            return evaluateNested(fact);
        }

        List<Object> fieldValue = getNestedValue(fact, field);
        return switch (fieldValue.size()) {
            case 0 -> evaluateCondition(null);
            case 1 -> evaluateCondition(fieldValue.getFirst());
            default -> fieldValue.stream().allMatch(this::evaluateCondition);
        };

    }

    private List<Object> getNestedValue(DynamicFact fact, String path) {
        if (path == null || path.isEmpty()) return null;
        Map<String, Object> fieldMap = fact.getFieldMap();
        List<Object> values = new ArrayList<>();
        Object value = fieldMap.getOrDefault(path, null);

        if (value == null) {
            var list = fieldMap.values().stream().filter(item -> item instanceof List<?>)
                    .flatMap(item-> ((List<Map<String, Object>>) item).stream())
                    .filter(item->item.containsKey(path))
                    .map(item -> item.get(path)).toList();
            values.addAll(list);

        } else {
            values.add(value);
        }

        return values;
    }

    private boolean evaluateNested(DynamicFact fact) {
        switch (operator) {
            case AND:
                return nestedConditions.stream().allMatch(c -> c.evaluate(fact));
            case OR:
                return nestedConditions.stream().anyMatch(c -> c.evaluate(fact));
            case NOT:
                return !nestedConditions.getFirst().evaluate(fact);
            default:
                return false;
        }
    }


    private boolean evaluateCondition(Object fieldValue) {
        switch (type) {
            case EQUALS:
                return objectEquals(fieldValue, value);

            case NOT_EQUALS:
                return !objectEquals(fieldValue, value);

            case GREATER_THAN:
                return compareNumbers(fieldValue, value) > 0;

            case GREATER_THAN_OR_EQUAL:
                return compareNumbers(fieldValue, value) >= 0;

            case LESS_THAN:
                return compareNumbers(fieldValue, value) < 0;

            case LESS_THAN_OR_EQUAL:
                return compareNumbers(fieldValue, value) <= 0;

            case CONTAINS:
                return containsValue(fieldValue, value);

            case NOT_CONTAINS:
                return !containsValue(fieldValue, value);

            case REGEX:
                return fieldValue != null && Pattern.matches(value.toString(), fieldValue.toString());

            case IN:
                if (value instanceof List) {
                    return ((List<?>) value).contains(fieldValue);
                }
                return false;

            case NOT_IN:
                if (value instanceof List) {
                    return !((List<?>) value).contains(fieldValue);
                }
                return true;

            case IS_NULL:
                return fieldValue == null;

            case IS_NOT_NULL:
                return fieldValue != null;

            case BETWEEN:
                int comp1 = compareNumbers(fieldValue, value);
                int comp2 = compareNumbers(fieldValue, value2);
                return comp1 >= 0 && comp2 <= 0;

            default:
                return false;
        }
    }

    private boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;

        // Convert to strings for comparison if types differ
        if (!obj1.getClass().equals(obj2.getClass())) {
            return obj1.toString().equals(obj2.toString());
        }

        return obj1.equals(obj2);
    }

    private int compareNumbers(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) return 0;

        double num1 = toDouble(obj1);
        double num2 = toDouble(obj2);

        return Double.compare(num1, num2);
    }

    private double toDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private boolean containsValue(Object fieldValue, Object searchValue) {
        if (fieldValue == null || searchValue == null) return false;

        if (fieldValue instanceof String) {
            return fieldValue.toString().contains(searchValue.toString());
        }

        if (fieldValue instanceof List) {
            return ((List<?>) fieldValue).contains(searchValue);
        }

        return false;
    }

    // Getters and setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public List<RuleCondition> getNestedConditions() {
        return nestedConditions;
    }

    public void setNestedConditions(List<RuleCondition> nestedConditions) {
        this.nestedConditions = nestedConditions;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }
}