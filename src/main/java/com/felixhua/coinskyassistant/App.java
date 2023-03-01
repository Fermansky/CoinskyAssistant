package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.ui.ContentScene;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = MainController.getInstance();
        ContentScene scene = new ContentScene(new MessagePane(controller), primaryStage);
        Crawler crawler = new Crawler(controller);
        SettingStage settingStage = new SettingStage(controller);

        VoiceAssistant paimon = new VoiceAssistant("paimon");
        paimon.setAvatar("paimon");
        settingStage.addVoiceAssistant(paimon);

        scene.setFill(new Color(0, 0, 0, 0.5));

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("钱币天堂上架提醒bot");
        primaryStage.getIcons().add(new Image(App.class.getResource("/icon.png").toExternalForm()));
        primaryStage.show();

        VoiceUtil.play(VoicePrompt.DAILY_GREETING);

        crawler.start();
    }

}
