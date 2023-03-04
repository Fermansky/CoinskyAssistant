package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.VoicePrompt;
import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class VoiceUtil {
    private static MediaPlayer mp;

    public static void play(VoiceAssistant assistant, VoicePrompt prompt) {
        if (mp != null && mp.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mp.dispose();
        }
        URL resource = VoiceUtil.class.getResource("/voice/" + assistant.getName() + "/"
                + prompt.getName() + ".mp3");
        if (resource == null) {
            System.err.println("resource not found");
            return ;
        }
        Media media = new Media(resource.toExternalForm());
        mp = new MediaPlayer(media);
        mp.volumeProperty().bind(MainController.getInstance().getSettingStage().getVolumeSlider().valueProperty());
        mp.play();
    }


    public static void play(VoicePrompt prompt) {
        VoiceAssistant assistant = MainController.getInstance().getVoiceAssistant();
        play(assistant, prompt);
    }

}
