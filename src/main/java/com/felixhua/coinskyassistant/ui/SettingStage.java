package com.felixhua.coinskyassistant.ui;

import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SettingStage extends Stage {
    private double offsetX, offsetY;
    private MainController controller;
    private Scene settingScene;
    private BorderPane outerPane;
    private BorderPane settingPane;
    private AnchorPane topBar;
    private AnchorPane contentPane;
    private HBox bottomBar; // presents tips
    private ChoiceBox<VoiceAssistant> voiceAssistantChoiceBox;
    private ImageView voiceAssistantView;
    private Slider volumeSlider;

    private void initTopBar() {
        topBar = new AnchorPane();
        topBar.getStyleClass().add("top-bar");
        topBar.setPrefSize(500, 40);

        ImageView iconView = new ImageView(MessagePane.class.getResource("/icon.png").toExternalForm());
        iconView.setFitWidth(30);
        iconView.setFitHeight(30);
        AnchorPane.setLeftAnchor(iconView, 5.0);
        AnchorPane.setTopAnchor(iconView, 5.0);

        Label titleLabel = new Label("钱币天堂助手控制面板");
        titleLabel.setStyle("-fx-font-weight: bold");
        titleLabel.setFont(new Font(15));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setPrefHeight(40);
        AnchorPane.setTopAnchor(titleLabel, 0.0);
        AnchorPane.setLeftAnchor(titleLabel, 40.0);

        BorderPane closeButton = new BorderPane();
        Region closeRegion = new Region();
        closeRegion.setMaxSize(25, 25);
        closeButton.getStyleClass().add("close-btn");
        closeButton.setPrefSize(40, 40);
        closeButton.setCenter(closeRegion);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnMouseReleased(event -> {
            this.hide();
        });
        AnchorPane.setRightAnchor(closeButton, 0.0);
        AnchorPane.setTopAnchor(closeButton, 0.0);

        topBar.getChildren().addAll(iconView, titleLabel, closeButton);
    }

    private void initContentPane() {
        contentPane = new AnchorPane();
        contentPane.setPrefSize(500, 240);

        Label logLabel = new Label("日志信息:");
        logLabel.getStyleClass().add("setting-label");
        AnchorPane.setLeftAnchor(logLabel, 10.0);
        AnchorPane.setTopAnchor(logLabel, 10.0);

        Button openLogFileButton = new Button("打开日志文件");
        openLogFileButton.setOnMousePressed(event -> {
            LogUtil.openLogFile();
        });
        AnchorPane.setTopAnchor(openLogFileButton, 10.0);
        AnchorPane.setRightAnchor(openLogFileButton, 270.0);

        Label soundSettingLabel = new Label("语音助手:");
        soundSettingLabel.getStyleClass().add("setting-label");
        AnchorPane.setLeftAnchor(soundSettingLabel, 260.0);
        AnchorPane.setTopAnchor(soundSettingLabel, 10.0);

        volumeSlider = new Slider();
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(1.0);
        volumeSlider.setPrefWidth(130);
        AnchorPane.setTopAnchor(volumeSlider, 13.0);
        AnchorPane.setLeftAnchor(volumeSlider, 350.0);

        voiceAssistantChoiceBox = new ChoiceBox<>();
        voiceAssistantChoiceBox.setMaxWidth(100);
        voiceAssistantChoiceBox.valueProperty().addListener(new ChangeListener<VoiceAssistant>() {
            @Override
            public void changed(ObservableValue<? extends VoiceAssistant> observable, VoiceAssistant oldValue, VoiceAssistant newValue) {
                voiceAssistantView.setImage(newValue.getAvatar());
            }
        });
        controller.voiceAssistantProperty.bind(voiceAssistantChoiceBox.valueProperty());
        AnchorPane.setLeftAnchor(voiceAssistantChoiceBox, 260.0);
        AnchorPane.setTopAnchor(voiceAssistantChoiceBox, 40.0);

        voiceAssistantView = new ImageView();
        voiceAssistantView.setFitHeight(130.0);
        voiceAssistantView.setFitWidth(130.0);
        voiceAssistantView.setEffect(new InnerShadow());
        voiceAssistantView.setOnMouseClicked(event -> {
            VoiceUtil.play(VoicePrompt.DAILY_GREETING);
        });
        AnchorPane.setLeftAnchor(voiceAssistantView, 350.0);
        AnchorPane.setTopAnchor(voiceAssistantView, 40.0);

        contentPane.getChildren().addAll(logLabel, soundSettingLabel, voiceAssistantChoiceBox,
                voiceAssistantView, volumeSlider, openLogFileButton);
    }

    private void initSettingPane() {
        settingPane = new BorderPane();
        settingPane.getStylesheets().add(MessagePane.class.getResource("/css/SettingPane.css").toExternalForm());
        settingPane.getStyleClass().add("setting-pane");
        settingPane.setPrefSize(500, 300);
        settingPane.setMaxSize(500, 300);

        initTopBar();
        initContentPane();
        bottomBar = new HBox();

        settingPane.setTop(topBar);
        settingPane.setCenter(contentPane);
        settingPane.setEffect(new DropShadow());
    }

    private void initSettingScene() {
        outerPane = new BorderPane();
        outerPane.setPrefSize(520, 320);
        outerPane.setMinSize(520, 320);
        outerPane.setBackground(null);
        outerPane.setCenter(settingPane);
        settingScene = new Scene(outerPane);
        settingScene.setFill(null);
    }

    private void setDraggable() {
        topBar.setOnMousePressed(event -> {
            if (event.getX() <= 460) {
                offsetX = event.getSceneX();
                offsetY = event.getSceneY();
                topBar.setCursor(Cursor.CLOSED_HAND);
            }
        });

        topBar.setOnMouseDragged(event -> {
            setX(event.getScreenX()-offsetX);
            setY(event.getScreenY()-offsetY);
        });

        topBar.setOnMouseReleased(event -> {
            topBar.setCursor(Cursor.DEFAULT);
        });
    }

    public void addVoiceAssistant(VoiceAssistant voiceAssistant) {
        voiceAssistantChoiceBox.getItems().add(voiceAssistant);
        if (voiceAssistantChoiceBox.getItems().size() == 1) {
            voiceAssistantChoiceBox.setValue(voiceAssistant);
        }
    }

    public Slider getVolumeSlider() {
        return this.volumeSlider;
    }

    public SettingStage(MainController controller) {
        this.controller = controller;
        controller.setSettingStage(this);

        initSettingPane();
        initSettingScene();

        setDraggable();
        setScene(settingScene);
        setTitle("钱币天堂bot控制面板");
        setResizable(false);
        initStyle(StageStyle.TRANSPARENT);
    }
}
