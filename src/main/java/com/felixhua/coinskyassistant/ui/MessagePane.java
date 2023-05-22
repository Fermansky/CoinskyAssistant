package com.felixhua.coinskyassistant.ui;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.GoodsItem;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagePane extends Pane {
    private boolean ready = false;
    private final MainController controller;
    private GoodsItem item;
    private ImageView imageView;
    private Label nameLabel;
    private Label priceLabel;
    private Label delayLabel;
    private StackPane closeButton;
    private StackPane settingButton;

    private void initNameLabel() {
        nameLabel = new Label("商品名称");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.TOP_LEFT);
        nameLabel.setMaxWidth(170);
        nameLabel.setMaxHeight(90);
        nameLabel.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        nameLabel.setFont(Font.font(20));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.relocate(200, 10);
    }

    private void initPriceLabel() {
        priceLabel = new Label("￥价格");
        priceLabel.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        priceLabel.setFont(Font.font(25));
        priceLabel.setTextFill(Color.RED);
        priceLabel.setAlignment(Pos.BOTTOM_LEFT);
        priceLabel.setMaxWidth(180);
        priceLabel.setPrefHeight(90);
        priceLabel.setMaxHeight(90);
        priceLabel.relocate(200, 100);
    }

    private void initDelayLabel() {
        delayLabel = new Label("0");
        delayLabel.getStyleClass().add("delay-label");
        delayLabel.setMaxSize(20, 20);
        delayLabel.setMinSize(20, 20);
        delayLabel.setAlignment(Pos.BOTTOM_RIGHT);
        delayLabel.setTextFill(Color.LAWNGREEN);
        delayLabel.relocate(370, 170);
    }

    private void initCloseButton() {
        closeButton = new StackPane();
        Region region = new Region();
        region.setMaxSize(18, 18);
        closeButton.setPrefSize(30, 30);
        closeButton.relocate(370, 0);
        closeButton.getStyleClass().add("close-button");
        closeButton.getChildren().add(region);
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnMousePressed(event -> {
            Platform.exit();
//            System.exit(0);
        });
    }

    private void initSettingButton() {
        settingButton = new StackPane();
        Region region = new Region();
        region.setMaxSize(18, 18);
        settingButton.setPrefSize(25, 25);
        settingButton.relocate(372, 40);
        settingButton.getStyleClass().add("setting-button");
        settingButton.getChildren().add(region);
        settingButton.setCursor(Cursor.HAND);
        settingButton.setOnMousePressed(event -> {
            SettingStage settingStage = controller.getSettingStage();
            if(settingStage.isShowing()) {
                settingStage.toFront();
            }
            else {
                settingStage.show();
            }
        });
    }

    private void initLayout() {
        setPrefHeight(200);
        setPrefWidth(400);
        setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

        imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.relocate(10, 10);
        imageView.setCursor(Cursor.HAND);
        imageView.setOnMouseClicked(event -> {
            if (item != null) {
                Desktop desktop = Desktop.getDesktop();
                if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        URI uri = new URI(item.getItemUrl());
                        desktop.browse(uri);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        initNameLabel();
        initPriceLabel();
        initDelayLabel();
        initCloseButton();
        initSettingButton();

        getChildren().addAll(imageView, nameLabel, priceLabel, delayLabel, closeButton, settingButton);
    }

    public void setDelayLabel(int delay) {
        // delay is in seconds
        Platform.runLater(() -> {
            delayLabel.setText(String.valueOf(delay));
            if (delay <= 5) {
                delayLabel.setTextFill(Color.LAWNGREEN);
            } else if (delay < 10) {
                delayLabel.setTextFill(Color.GOLD);
            } else {
                delayLabel.setTextFill(Color.RED);
            }
        });
    }

    public void updateItem(GoodsItem item) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!isReady()) {
            if (date.equals(item.getTime().split(" ")[0])) {
                if (item.getPrice().endsWith("议价")) {
                    VoiceUtil.play(VoicePrompt.END_OF_DAY);
                }
                else VoiceUtil.play(VoicePrompt.STARTED);
            }
            else VoiceUtil.play(VoicePrompt.NOT_STARTED);
        } else {
            if (item.getPrice().endsWith("议价")) {
                VoiceUtil.play(VoicePrompt.END_OF_DAY);
            }
            else VoiceUtil.play(VoicePrompt.NEW_ITEM);
        }
        this.item = item;
        Platform.runLater(() -> {
            this.imageView.setImage(item.getImage());
            this.nameLabel.setText(item.getName());
            this.priceLabel.setText(item.getPrice());
            priceLabel.setOpacity(1);
            if (!ready) {
                ready = true;
            }
        });
    }

    public void itemSold() {
        Platform.runLater(() -> {
            this.priceLabel.setOpacity(0.5);
        });
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void processLatestItem(GoodsItem latestItem) {
        if (item == null || !item.getName().equals(latestItem.getName())) {
            updateItem(latestItem);
            LogUtil.log("新上架货品 " + latestItem + " 。");
            if (latestItem.getStatus().equals("已售")) {
                itemSold();
            }
        } else if (latestItem.getStatus().equals("已售") && item.getStatus().equals("待售")) {
            this.item = latestItem;
            itemSold();
            LogUtil.log("商品 " + latestItem.getName() + " 已售出。");
            VoiceUtil.play(VoicePrompt.ITEM_SOLD);
        } else {
            LogUtil.log("无新上架商品。");
        }
    }

    public MessagePane (MainController mainController) {
        controller = mainController;
        controller.setMessagePane(this);
        this.getStylesheets().add(MessagePane.class.getResource("/css/MessagePane.css").toExternalForm());

        initLayout();
    }
}
