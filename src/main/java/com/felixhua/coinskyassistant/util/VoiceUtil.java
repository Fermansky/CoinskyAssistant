package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.VoicePrompt;
import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import javafx.beans.property.DoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class VoiceUtil {
    private static MediaPlayer mp;

    public static void play(VoiceAssistant assistant, VoicePrompt prompt) {
        if (mp != null && mp.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mp.dispose();
        }
        Media media = new Media(VoiceUtil.class.getResource("/voice/" + assistant.getName() + "/"
                + prompt.getName() + ".mp3").toExternalForm());
        mp = new MediaPlayer(media);
        mp.volumeProperty().bind(MainController.getInstance().getSettingStage().getVolumeSlider().valueProperty());
        mp.play();
    }


    public static void play(VoicePrompt prompt) {
        VoiceAssistant assistant = MainController.getInstance().getVoiceAssistant();
        play(assistant, prompt);
    }

}
