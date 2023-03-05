package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.enums.LogLevel;

import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static BufferedWriter writer;
    private static File logFile;
    private static DateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String logFileName = new SimpleDateFormat("yyyy-MM-dd hhmmss").format(new Date());
        logFile = new File("./log/" + logFileName + ".log");
        try {
            writer = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}

