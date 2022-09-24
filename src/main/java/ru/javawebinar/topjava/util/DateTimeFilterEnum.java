package ru.javawebinar.topjava.util;

public enum DateTimeFilterEnum {
    DATE_FROM("dateFrom"),
    DATE_TO("dateTo"),
    TIME_FROM("timeFrom"),
    TIME_TO("timeTo");

    private final String paramName;

    DateTimeFilterEnum(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
