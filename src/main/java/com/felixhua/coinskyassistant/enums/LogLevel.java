package com.felixhua.coinskyassistant.enums;

public enum LogLevel {
    SEVERE("SEVERE"), WARNING("WARNING"), INFO("INFO");

    private final String name;

    public String getName() {
        return this.name;
    }

    LogLevel(String name) {
        this.name = name;
    }
}
