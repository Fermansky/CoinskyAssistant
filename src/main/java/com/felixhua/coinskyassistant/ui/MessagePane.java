package com.felixhua.coinskyassistant.ui;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.ItemVO;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.util.HttpsUtil;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MessagePane extends Pane {
    /**
     * whether the program has started.
     */
    private boolean ready = false;
    private final String date;
    private final MainController controller;
    private ItemVO itemVO;
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
        delayLabel.setMaxSize(30, 20);
        delayLabel.setMinSize(30, 20);
        delayLabel.setAlignment(Pos.BOTTOM_RIGHT);
        delayLabel.setTextFill(Color.LAWNGREEN);
        delayLabel.relocate(360, 170);
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
            if (itemVO != null) {
                HttpsUtil.browse(itemVO.getUrl());
            }
        });

        initNameLabel();
        initPriceLabel();
        initDelayLabel();
        initCloseButton();
        initSettingButton();

        getChildren().addAll(imageView, nameLabel, priceLabel, delayLabel, closeButton, settingButton);
    }

    private void playVoiceOnUpdate(ItemVO itemVO) {
        if (!isReady()) {   // 首次更新
            if (date.equals(itemVO.getTime().split(" ")[0])) {
                if (itemVO.getFormattedPrice().equals("¥议价")) {
                    VoiceUtil.play(VoicePrompt.END_OF_DAY);
                }
                else VoiceUtil.play(VoicePrompt.STARTED);
            }
            else VoiceUtil.play(VoicePrompt.NOT_STARTED);
        } else {
            if (itemVO.getFormattedPrice().equals("¥议价")) {
                VoiceUtil.play(VoicePrompt.END_OF_DAY);
            } else if (itemVO.getId() != this.itemVO.getId()) {
                VoiceUtil.play(VoicePrompt.NEW_ITEM);
            }
        }
    }

    /**
     * Update the content of the message pane.
     * @param itemVO the latest item
     */
    public void updateItem(ItemVO itemVO) {
        if(itemVO == null || itemVO.equals(this.itemVO)) {
            return ;
        }
        playVoiceOnUpdate(itemVO);

        this.imageView.setImage(itemVO.getThumbnailImage());
        this.nameLabel.setText(itemVO.getName());
        this.priceLabel.setText(itemVO.getFormattedPrice());
        priceLabel.setOpacity(1);

        if (this.itemVO != null && this.itemVO.getStatus() == 0 && itemVO.getStatus() == 2) {
            itemSold();
        }
        this.itemVO = itemVO;
        if (!ready) {
            if(itemVO.getStatus() == 2) {
                priceLabel.setOpacity(0.5);
            }
            ready = true;
        }
    }

    public void itemSold() {
        this.priceLabel.setOpacity(0.5);
        VoiceUtil.play(VoicePrompt.ITEM_SOLD);
        LogUtil.log("商品 " + itemVO.getName() + " 已售出。");
    }

    public boolean isReady() {
        return ready;
    }

    public void updateDelayLabel(int delay) {
        // delay is in milliseconds
        delayLabel.setText(String.valueOf(delay));
        if (delay < 1000) {
            delayLabel.setTextFill(Color.LAWNGREEN);
        } else if (delay < 2000) {
            delayLabel.setTextFill(Color.GOLD);
        } else {
            delayLabel.setTextFill(Color.RED);
        }
    }

    public MessagePane (MainController mainController) {
        controller = mainController;
        controller.setMessagePane(this);
        this.getStylesheets().add(Objects.requireNonNull(MessagePane.class.getResource("/css/MessagePane.css")).toExternalForm());

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.date = simpleDateFormat.format(date);
        initLayout();
    }
}
