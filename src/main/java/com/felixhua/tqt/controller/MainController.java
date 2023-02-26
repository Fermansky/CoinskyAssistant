package com.felixhua.tqt.controller;

import com.felixhua.tqt.Crawler;
import com.felixhua.tqt.ui.MessagePane;
import com.felixhua.tqt.ui.SettingStage;

import java.util.Date;

public class MainController {
    public MessagePane messagePane;
    public Crawler crawler;
    public SettingStage settingStage;

    public void log(String info) {
        settingStage.appendLog(new Date() + " " + info + "\n");
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    public void setMessagePane(MessagePane messagePane) {
        this.messagePane = messagePane;
    }

    public void setSettingStage(SettingStage settingStage) {
        this.settingStage = settingStage;
    }

    public Crawler getCrawler() {
        return crawler;
    }

    public MessagePane getMessagePane() {
        return messagePane;
    }

    public SettingStage getSettingStage() {
        return settingStage;
    }
}
