package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.HttpsClient;
import javafx.concurrent.Task;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

public class CrawlingTask extends Task<String> {
    @Override
    protected String call() throws Exception {
        long startTimeMillis = System.currentTimeMillis();
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("www.yy11.com")
                .setPath("api/")
//                        .addParameter("callback", "jQuery224017221595746282725_1685557238775")
                .addParameter("m", "shop")
                .addParameter("c", "lists")
                .addParameter("v", "search")
                .addParameter("data", "{\"id\":\"2636\",\"perPage\":10,\"pageId\":0}")
//                        .addParameter("_", "1685557238776")
                .build();
        String result = HttpsClient.sendGet(uri.toString());
//        System.out.println("爬取成功，耗时: " + (System.currentTimeMillis() - startTimeMillis) + " 毫秒");
        updateMessage(String.valueOf(System.currentTimeMillis() - startTimeMillis));
        return result;
    }
}
