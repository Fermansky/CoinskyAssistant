package com.felixhua.coinskyassistant.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties;

    static {
        // 加载db.properties配置文件
        properties = new Properties();
        try {
            URL resource = DatabaseUtil.class.getResource("/db.properties");
            if (resource != null) {
                properties.load(new FileInputStream(new File(resource.toURI())));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
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
            throw new RuntimeException(e);
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
