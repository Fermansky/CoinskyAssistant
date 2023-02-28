package com.felixhua.tqt.ui;

import com.felixhua.tqt.controller.MainController;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class SettingStage extends Stage {
    private double offsetX, offsetY;
    private MainController controller;
    private Scene settingScene;
    private BorderPane settingPane;
    private AnchorPane topBar;
    private AnchorPane contentPane;
    private HBox bottomBar; // presents tips

    private TextArea logArea;

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
        AnchorPane.setRightAnchor(closeButton, 0.0);
        AnchorPane.setTopAnchor(closeButton, 0.0);
        closeButton.setCenter(closeRegion);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnMousePressed(event -> {
            this.hide();
        });

        topBar.getChildren().addAll(iconView, titleLabel, closeButton);
    }

    private void initContentPane() {
        contentPane = new AnchorPane();
        contentPane.setPrefSize(500, 240);

        Label logLabel = new Label("日志信息:");
        AnchorPane.setLeftAnchor(logLabel, 10.0);
        AnchorPane.setTopAnchor(logLabel, 10.0);

        logArea = new TextArea();
        logArea.setPrefSize(220, 200);
        logArea.setPrefRowCount(10);
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.getStyleClass().removeAll("text-input", "text-area");
        logArea.getStyleClass().add("log-area");
//        System.out.println(logArea.getStyleClass());
        AnchorPane.setLeftAnchor(logArea, 10.0);
        AnchorPane.setBottomAnchor(logArea, 20.0);

        contentPane.getChildren().addAll(logLabel, logArea);
    }

    private void initSettingPane() {
        settingPane = new BorderPane();
        settingPane.getStylesheets().add(MessagePane.class.getResource("/css/SettingPane.css").toExternalForm());
        settingPane.getStyleClass().add("setting-pane");
        settingPane.setPrefSize(500, 300);

        initTopBar();
        initContentPane();
        bottomBar = new HBox();

        settingPane.setTop(topBar);
        settingPane.setCenter(contentPane);
    }

    private void initSettingScene() {
        settingScene = new Scene(settingPane);
        settingScene.setFill(new Color(0, 0, 0, 0));
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

    public void appendLog(String log) {
        logArea.appendText(log);
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
