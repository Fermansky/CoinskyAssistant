package com.felixhua.coinskyassistant.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static BufferedWriter writer;
    private static File logFile;

    static {
        String logFileName = new SimpleDateFormat("yyyy-MM-dd hhmmss").format(new Date());
        logFile = new File("./" + logFileName + ".log");
        try {
            writer = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendLog(String logMessage) {
        try {
            writer.write(logMessage);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
