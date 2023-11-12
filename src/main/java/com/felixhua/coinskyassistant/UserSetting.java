package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 用于存储用户设置，用户使用时将沿用上一次使用的设置。
 */
public class UserSetting {
    /**
     * 默认的配置文件路径。
     */
    private static String configFilePath = "./config.properties";
    private static Properties properties = new Properties();
    /**
     * 是否启用mirai，实验性
     */
    public static boolean mirai = false;

    /**
     * 记录语音助手的名字。
     */
    public static String assistant = "paimon";

    public static void load() {
        load(new File(configFilePath));
    }

    public static void load(File file) {
        if(!file.exists()) {
            LogUtil.warn("未能找到配置文件，将使用默认配置。");
            return ;
        }
        try(FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (IOException e) {
            LogUtil.warn(e.getMessage());
        }
        if(properties.isEmpty()) {
            LogUtil.warn("配置文件加载失败，将使用默认配置。");
            return;
        }
        MainController.getInstance().setVoiceAssistant(properties.getProperty("assistant"));
    }

    public static void save() {
        File file = new File(configFilePath);
        try(FileOutputStream output = new FileOutputStream(file)) {
            refreshProperties();
            properties.store(output, "User Settings");
        } catch (IOException e) {
            LogUtil.warn(e.getMessage());
        }
    }

    private static void refreshProperties() {
        properties.clear();
        properties.put("mirai", String.valueOf(mirai));
        properties.put("assistant", MainController.getInstance().getVoiceAssistant().getName());
    }
}
