package com.example.admin.rag.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器表达式，对齐 Spring AI {@code Filter.Expression}。
 * <p>
 * SimpleVectorStore 对复杂 Filter 支持有限，当前仅用于声明过滤意图，
 * 实际过滤在 RetrievalService 内存层兜底执行。
 * 切换 PgVector 后改为 SQL WHERE 子句生成，移除此兜底逻辑。
 * </p>
 */
public class FilterExpression {

    public enum Operator {
        EQ, NE, GT, GTE, LT, LTE, IN, LIKE
    }

    public enum Logic {
        AND, OR
    }

    private String key;
    private Operator operator;
    private Object value;
    private Logic logic;
    private List<FilterExpression> children;

    public FilterExpression() {}

    private FilterExpression(String key, Operator op, Object value) {
        this.key = key;
        this.operator = op;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public static FilterExpression eq(String key, Object value) {
        return new FilterExpression(key, Operator.EQ, value);
    }

    public static FilterExpression gt(String key, Object value) {
        return new FilterExpression(key, Operator.GT, value);
    }

    public static FilterExpression gte(String key, Object value) {
        return new FilterExpression(key, Operator.GTE, value);
    }

    public static FilterExpression lt(String key, Object value) {
        return new FilterExpression(key, Operator.LT, value);
    }

    public static FilterExpression lte(String key, Object value) {
        return new FilterExpression(key, Operator.LTE, value);
    }

    public static FilterExpression ne(String key, Object value) {
        return new FilterExpression(key, Operator.NE, value);
    }

    public static FilterExpression like(String key, Object value) {
        return new FilterExpression(key, Operator.LIKE, value);
    }

    public static FilterExpression in(String key, List<?> values) {
        return new FilterExpression(key, Operator.IN, values);
    }

    public static FilterExpression and(FilterExpression... exprs) {
        FilterExpression parent = new FilterExpression();
        parent.logic = Logic.AND;
        parent.children = new ArrayList<>();
        for (FilterExpression e : exprs) parent.children.add(e);
        return parent;
    }

    public static FilterExpression or(FilterExpression... exprs) {
        FilterExpression parent = new FilterExpression();
        parent.logic = Logic.OR;
        parent.children = new ArrayList<>();
        for (FilterExpression e : exprs) parent.children.add(e);
        return parent;
    }

    /** 评估此过滤器是否匹配给定文档 */
    public boolean matches(Document doc) {
        if (children != null && !children.isEmpty()) {
            if (logic == Logic.AND) {
                return children.stream().allMatch(c -> c.matches(doc));
            } else {
                return children.stream().anyMatch(c -> c.matches(doc));
            }
        }
        if (key == null || operator == null) return true;

        Object docVal = doc.getMetadata() != null ? doc.getMetadata().get(key) : null;
        if (docVal == null) return false;

        switch (operator) {
            case EQ: return compareEq(docVal, value);
            case NE: return !compareEq(docVal, value);
            case GT: return compareNumeric(docVal, value) > 0;
            case GTE: return compareNumeric(docVal, value) >= 0;
            case LT: return compareNumeric(docVal, value) < 0;
            case LTE: return compareNumeric(docVal, value) <= 0;
            case LIKE: return docVal.toString().toLowerCase().contains(value.toString().toLowerCase());
            case IN:
                if (value instanceof List) {
                    for (Object v : (List<?>) value) {
                        if (compareEq(docVal, v)) return true;
                    }
                }
                return false;
            default: return false;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean compareEq(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).doubleValue() == ((Number) b).doubleValue();
        }
        if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable) a).compareTo(b) == 0;
        }
        return a.toString().equals(b.toString());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static int compareNumeric(Object a, Object b) {
        double da = (a instanceof Number) ? ((Number) a).doubleValue() : Double.NaN;
        double db = (b instanceof Number) ? ((Number) b).doubleValue() : Double.NaN;
        return Double.compare(da, db);
    }

    public String getKey() { return key; }
    public Operator getOperator() { return operator; }
    public Object getValue() { return value; }
    public Logic getLogic() { return logic; }
    public List<FilterExpression> getChildren() { return children; }

    @Override
    public String toString() {
        if (children != null && !children.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) sb.append(" ").append(logic).append(" ");
                sb.append(children.get(i).toString());
            }
            sb.append(")");
            return sb.toString();
        }
        if (key == null) return "ALL";
        return key + " " + operator + " " + value;
    }
}
