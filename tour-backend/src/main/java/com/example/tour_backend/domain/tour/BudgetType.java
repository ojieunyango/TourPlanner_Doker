package com.example.tour_backend.domain.tour;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BudgetType {
    LOW("low"),
    MEDIUM("medium"), 
    HIGH("high"),
    LUXURY("luxury");

    private final String value;

    BudgetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BudgetType fromValue(String value) {
        if (value == null) {
            return MEDIUM; // 기본값
        }
        
        for (BudgetType budget : BudgetType.values()) {
            if (budget.value.equalsIgnoreCase(value)) {
                return budget;
            }
        }
        
        // 잘못된 값이 들어오면 기본값 반환
        return MEDIUM;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}