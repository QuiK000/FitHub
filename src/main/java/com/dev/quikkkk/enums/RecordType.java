package com.dev.quikkkk.enums;

public enum RecordType {
    MAX_WEIGHT,
    MAX_REPS,
    MAX_DISTANCE,
    BEST_TIME;

    public boolean isBetter(double newValue, double currentValue) {
        return this == BEST_TIME
                ? newValue < currentValue
                : newValue > currentValue;
    }

    public double improvement(double newValue, double currentValue) {
        return this == BEST_TIME
                ? currentValue - newValue
                : newValue - currentValue;
    }
}
