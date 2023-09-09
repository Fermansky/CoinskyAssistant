package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.enums.LogLevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties;

    static {
        // 加载db.properties配置文件
        properties = new Properties();
        try {
            InputStream inputStream = DatabaseUtil.class.getResourceAsStream("/db.properties");
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            LogUtil.log(LogLevel.WARNING, e.getMessage());
        }
    }

    public static void backupDatabase(File outputFile) {
        // 从配置文件中获取数据库信息
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String dbName = properties.getProperty("dbname");

        // 构建备份命令
        String[] commands = {"mysqldump", "-u"+username, "-p"+password, dbName};

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.redirectOutput(outputFile);
            pb.start();
        } catch (IOException e) {
            LogUtil.warn(e.getMessage());
        }
    }

    public static void backupDatabase() {
        String dbName = properties.getProperty("dbname");

        // 格式化当前日期时间作为备份文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timestamp = sdf.format(new Date());
        String backupFileName = dbName + "_" + timestamp + ".sql";

        File outputFile = new File("D:\\BaiduSyncdisk", backupFileName);
        backupDatabase(outputFile);
    }
}
