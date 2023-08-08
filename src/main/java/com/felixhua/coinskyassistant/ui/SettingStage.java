package com.felixhua.coinskyassistant.ui;

import com.felixhua.coinskyassistant.App;
import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.ItemDTO;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.service.CrawlingService;
import com.felixhua.coinskyassistant.util.AlertUtil;
import com.felixhua.coinskyassistant.util.DatabaseUtil;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.felixhua.coinskyassistant.util.AlertUtil.displayAlert;

public class SettingStage extends Stage {
    private final String TITLE = "钱币天堂智能助手控制面板";
    private final String DEFAULT_TIP = "准备就绪";

    private double offsetX, offsetY;
    private final MainController controller;
    private Scene settingScene;
    private BorderPane settingPane;
    private AnchorPane topBar;
    private AnchorPane contentPane;
    private HBox bottomBar; // presents tips
    private Label bottomInfo;
    private ChoiceBox<VoiceAssistant> voiceAssistantChoiceBox;
    private ImageView voiceAssistantView;
    private Slider volumeSlider;

    private void initTopBar() {
        topBar = new AnchorPane();
        topBar.getStyleClass().add("top-bar");
        topBar.setPrefSize(500, 40);

        ImageView iconView = new ImageView(Objects.requireNonNull(MessagePane.class.getResource("/icon.png")).toExternalForm());
        iconView.setFitWidth(30);
        iconView.setFitHeight(30);

        Label titleLabel = new Label(TITLE);
        titleLabel.setStyle("-fx-font-weight: bold");
        titleLabel.setFont(new Font(15));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setPrefHeight(40);

        HBox topHBox = new HBox(iconView, titleLabel);
        topHBox.setPrefHeight(40);
        topHBox.setAlignment(Pos.CENTER_LEFT);
        topHBox.setSpacing(5);
        AnchorPane.setLeftAnchor(topHBox, 5.0);

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

        topBar.getChildren().addAll(topHBox, closeButton);
    }

    private void initBottomBar() {
        bottomBar = new HBox();
        bottomBar.setPrefHeight(20);
        bottomBar.getStyleClass().add("bottom-bar");

        bottomInfo = new Label(DEFAULT_TIP);
        bottomBar.getChildren().add(bottomInfo);
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

        TextField updateField = new TextField("30");
        updateField.setMaxWidth(50);
        AnchorPane.setTopAnchor(updateField, 40.0);
        AnchorPane.setLeftAnchor(updateField, 10.0);
        Button updateInfoButton = new Button("更新数据库");
        updateInfoButton.setOnMousePressed(event -> {
            String inputText = updateField.getText();
            try {
                int number = Integer.parseInt(inputText);
                if (number >= 1 && number <= 50) {
                    ItemDTO itemDTO = CrawlingService.crawlAndUpdate(number);
                    displayAlert("更新成功", "成功拉取至" + itemDTO.getCreateTime() + "的数据。");
                } else {
                    displayAlert("非法输入", "请输入1~50之间的数！");
                }
            } catch (NumberFormatException ex) {
                displayAlert("非法输入", "请输入有效的整数！");
            }
        });
        AnchorPane.setTopAnchor(updateInfoButton, 40.0);
        AnchorPane.setRightAnchor(updateInfoButton, 270.0);

        Label testLabel = new Label("测试功能:");
        testLabel.getStyleClass().add("setting-label");
        AnchorPane.setLeftAnchor(testLabel, 10.0);
        AnchorPane.setTopAnchor(testLabel, 70.0);

        Button testButton =  new Button("测试功能");
        testButton.setOnMousePressed(event -> {
            FileChooser fileChooser = new FileChooser();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(new Date());
            String backupFileName = "CoinskyAssistant" + "_" + timestamp + ".sql";
            fileChooser.setInitialFileName(backupFileName);
            fileChooser.setTitle("设置文件保存的路径");
            File file = fileChooser.showSaveDialog(MainController.getInstance().getSettingStage());
            DatabaseUtil.backupDatabase(file);
            AlertUtil.displayAlert("数据库转储成功", "数据库文件已经被成功转储至" + file.getAbsolutePath());
        });
        AnchorPane.setTopAnchor(testButton, 70.0);
        AnchorPane.setRightAnchor(testButton, 270.0);

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
        voiceAssistantChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> voiceAssistantView.setImage(newValue.getAvatar()));
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
        voiceAssistantView.setOnMouseEntered(event -> {
            bottomInfo.setText(voiceAssistantChoiceBox.getValue().getDescription());
        });
        voiceAssistantView.setOnMouseExited(event -> {
            bottomInfo.setText(DEFAULT_TIP);
        });
        AnchorPane.setLeftAnchor(voiceAssistantView, 350.0);
        AnchorPane.setTopAnchor(voiceAssistantView, 40.0);

        contentPane.getChildren().addAll(logLabel, soundSettingLabel, voiceAssistantChoiceBox, updateField,
                voiceAssistantView, volumeSlider, openLogFileButton, updateInfoButton, testLabel, testButton);
    }

    private void initSettingPane() {
        settingPane = new BorderPane();
        settingPane.getStylesheets().add(Objects.requireNonNull(MessagePane.class.getResource("/css/SettingPane.css")).toExternalForm());
        settingPane.getStyleClass().add("setting-pane");
        settingPane.setPrefSize(500, 300);
        settingPane.setMaxSize(500, 300);

        initTopBar();
        initContentPane();
        initBottomBar();

        settingPane.setTop(topBar);
        settingPane.setCenter(contentPane);
        settingPane.setBottom(bottomBar);
        settingPane.setEffect(new DropShadow());
    }

    private void initSettingScene() {
        BorderPane outerPane = new BorderPane();
        outerPane.setPrefSize(520, 320);
        outerPane.setMinSize(520, 320);
        outerPane.setBackground(null);
        outerPane.setCenter(settingPane);
        settingScene = new Scene(outerPane);
        settingScene.setFill(null);
    }

    /**
     * 使顶部的标题条可以拖动。
     */
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

    public Label getBottomInfo() {
        return bottomInfo;
    }

    public SettingStage(MainController controller) {
        this.controller = controller;
        controller.setSettingStage(this);

        initSettingPane();
        initSettingScene();

        setDraggable();
        setScene(settingScene);
        setTitle(TITLE);
        setResizable(false);
        getIcons().add(new Image(Objects.requireNonNull(App.class.getResource("/icon.png")).toExternalForm()));
        initStyle(StageStyle.TRANSPARENT);
    }
}
