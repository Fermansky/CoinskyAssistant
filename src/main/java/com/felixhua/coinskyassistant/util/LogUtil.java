package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.enums.LogLevel;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static BufferedWriter writer;
    private static File logFile;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        String logDirectoryPath = "./log/";
        File logDirectory = new File(logDirectoryPath);
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }
        try {
            createLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createLogFile() throws IOException {
        String logFileName = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        logFile = new File("./log/" + logFileName + ".log");
        writer = new BufferedWriter(new FileWriter(logFile));
    }

    public static void openLogFile() {
        try {
            Desktop.getDesktop().open(logFile);
        } catch (IOException e) {
            log(LogLevel.SEVERE, e.getMessage());
        }
    }

    public static void log(LogLevel logLevel, String logMessage) {
        try {
            writer.write("[" + logLevel + "]" + dateFormat.format(new Date()) + "\t");
            writer.write(logMessage);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String logMessage) {
        log(LogLevel.INFO, logMessage);
    }

    public static void warn(String warningMessage) {
        log(LogLevel.WARNING, warningMessage);
    }
}

