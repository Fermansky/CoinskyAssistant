package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class VoiceUtil {
    private static MediaPlayer mp;

    public static void play(VoiceAssistant assistant, VoicePrompt prompt) {
        if (mp != null && (mp.getStatus().equals(MediaPlayer.Status.PLAYING) || mp.getStatus().equals(MediaPlayer.Status.UNKNOWN))) {
            mp.dispose();
        }
        String path = "/voice/" + assistant.getName() + "/" + prompt.getName() + ".mp3";
        URL resource = VoiceUtil.class.getResource(path);
        if (resource == null) {
            LogUtil.log(LogLevel.SEVERE, "未找到音频资源 " + path + " 。");
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
