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
        // ����db.properties�����ļ�
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
        // �������ļ��л�ȡ���ݿ���Ϣ
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String dbName = properties.getProperty("dbname");

        // ������������
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

        // ��ʽ����ǰ����ʱ����Ϊ�����ļ���
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timestamp = sdf.format(new Date());
        String backupFileName = dbName + "_" + timestamp + ".sql";

        File outputFile = new File("D:\\BaiduSyncdisk", backupFileName);
        backupDatabase(outputFile);
    }
}
