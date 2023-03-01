package com.felixhua.coinskyassistant;

public enum VoicePrompt {
    DAILY_GREETING("daily_greeting"),
    GREETING("greeting"),
    NOT_STARTED("not_started"),
    STARTED("started"),
    NEW_ITEM("new_item"),
    ITEM_SOLD("item_sold"),
    END_OF_DAY("end_of_day");

    private String name;

    public String getName() {
        return name;
    }

    VoicePrompt(String name) {
        this.name = name;
    }
}
