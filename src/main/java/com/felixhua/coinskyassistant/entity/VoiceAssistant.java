package com.felixhua.coinskyassistant.entity;

import javafx.scene.image.Image;

import java.util.Objects;

public class VoiceAssistant {
    private String name;
    private Image avatar;
    private String localizedName;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getDescription() {
        return description;
    }

    public void setAvatar(String avatarName) {
        this.avatar = new Image(Objects.requireNonNull(VoiceAssistant.class.getResource("/img/avatar/" +
                avatarName + ".jpg")).toExternalForm());
    }

    public Image getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        if (localizedName != null) {
            return localizedName;
        }
        return name;
    }

    public VoiceAssistant(String name) {
        this.name = name;
    }

    public VoiceAssistant(String name, String localizedName) {
        this.name = name;
        this.localizedName = localizedName;
    }
}
