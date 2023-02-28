package com.felixhua.coinskyassistant.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundUtil {

    public static MediaPlayer mp;

    public static void play(String soundName) {
        Media media = new Media(SoundUtil.class.getResource("/sound/" + soundName).toExternalForm());
        mp = new MediaPlayer(media);
        mp.play();
    }
}
