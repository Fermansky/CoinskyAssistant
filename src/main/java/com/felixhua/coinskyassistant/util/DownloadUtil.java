package com.felixhua.coinskyassistant.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUtil {
    public static void download(String resource, String localPath) throws MalformedURLException {
        File outputFile = new File(localPath);

        if (outputFile.exists()) {
            return;
        }

        URL url = new URL(resource);
        try (InputStream in = url.openStream();
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
