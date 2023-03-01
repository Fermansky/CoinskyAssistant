package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.Crawler;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Date;

public class MainController {
    private static MainController mainController = new MainController();
    public MessagePane messagePane;
    public Crawler crawler;
    public SettingStage settingStage;
    public ObjectProperty<VoiceAssistant> voiceAssistantProperty = new SimpleObjectProperty<>();

    public static MainController getInstance() {
        return mainController;
    }

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

    public VoiceAssistant getVoiceAssistant() {
        return voiceAssistantProperty.get();
    }

    private MainController() {

    }
}
