package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.controller.MainController;
import javafx.scene.control.Alert;

public class AlertUtil {
    public static void displayAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(MainController.getInstance().getSettingStage());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
