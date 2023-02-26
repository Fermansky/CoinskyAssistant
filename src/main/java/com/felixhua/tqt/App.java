package com.felixhua.tqt;

import com.felixhua.tqt.controller.MainController;
import com.felixhua.tqt.ui.ContentScene;
import com.felixhua.tqt.ui.MessagePane;
import com.felixhua.tqt.ui.SettingStage;
import com.felixhua.tqt.util.SoundUtil;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = new MainController();
        ContentScene scene = new ContentScene(new MessagePane(controller), primaryStage);
        Crawler crawler = new Crawler(controller);
        SettingStage settingStage = new SettingStage(controller);

        scene.setFill(new Color(0, 0, 0, 0.5));

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("钱币天堂上架提醒bot");
        primaryStage.getIcons().add(new Image(App.class.getResource("/icon.png").toExternalForm()));
        primaryStage.show();

        SoundUtil.play("greeting.mp3");

        crawler.start();
    }

}
