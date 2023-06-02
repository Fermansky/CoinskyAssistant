package com.felixhua.coinskyassistant.entity;

import javafx.scene.image.Image;

public class VoiceAssistant {
    private String name;
    private Image avatar;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAvatar(String avatarName) {
        this.avatar = new Image(VoiceAssistant.class.getResource("/img/avatar/" + avatarName + ".jpg").toExternalForm());
    }

    public Image getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return name;
    }

    public VoiceAssistant(String name) {
        this.name = name;
    }
}
