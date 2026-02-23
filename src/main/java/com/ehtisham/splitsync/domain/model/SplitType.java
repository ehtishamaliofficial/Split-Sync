package com.ehtisham.splitsync.domain.model;

public enum SplitType {
    EQUAL, PERCENTAGE, RATIO;

    public static SplitType from(String value) {
        return SplitType.valueOf(value.toUpperCase());
    }
}
