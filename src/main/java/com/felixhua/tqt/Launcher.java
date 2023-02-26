package com.felixhua.tqt;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // 关闭lcd抗锯齿 (JavaFX默认自带)
        System.setProperty("prism.lcdtext", "false");

        Application.launch(App.class);
    }
}
