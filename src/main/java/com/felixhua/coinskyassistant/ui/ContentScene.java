package com.felixhua.coinskyassistant.ui;

import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ContentScene extends Scene {
    private double offsetX, offsetY;
    private final Stage stage;
    public ContentScene(Parent root, Stage stage) {
        super(root);
        this.stage = stage;
        setCanDrag();
    }


    public void setCanDrag() {

        this.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
            setCursor(Cursor.CLOSED_HAND);
        });

        this.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX()-offsetX);
            stage.setY(event.getScreenY()-offsetY);
        });

        this.setOnMouseReleased(event -> {
            setCursor(Cursor.DEFAULT);
        });
    }
}
