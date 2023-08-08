package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.controller.MainController;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class AlertUtil {
    public static void displayAlert(String title, String content) {
        displayAlert(Alert.AlertType.INFORMATION, title, content);
    }

    public static void displayAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.initOwner(MainController.getInstance().getSettingStage());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(initContentLabel(content));
        alert.showAndWait();
    }

    /**
     * 需要自定义内容标签的原因是其默认的contentLabel在面对较长的文件路径文本时显示效果较差
     * @param content
     * @return
     */
    private static Label initContentLabel(String content) {
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(300);
        return contentLabel;
    }
}
