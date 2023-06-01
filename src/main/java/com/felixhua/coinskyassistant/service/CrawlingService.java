package com.felixhua.coinskyassistant.service;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class CrawlingService extends ScheduledService<String> {
    public CrawlingService() {
        this.setPeriod(Duration.seconds(2));
        this.setRestartOnFailure(true);
    }

    @Override
    protected Task<String> createTask() {
        return new CrawlingTask();
    }
}
