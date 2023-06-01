package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.constants.Constant;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * &#064;Author  ZhangLe
 * &#064;Date  2020/11/25 12:33
 */
public class HttpsUtil {

    private static CloseableHttpClient httpClient;
    private static HttpGet httpGet;
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * 发送get请求
     * @param url 发送链接 拼接参数
     */
    public static String sendGet(String url) throws IOException {
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet(url);
        httpGet.setHeader(new BasicHeader("Referer", Constant.TAO_URL));
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return resp;
    }
}

