package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.controller.CrawlingController;
import com.felixhua.coinskyassistant.entity.CrawlingData;
import com.felixhua.coinskyassistant.util.HttpsUtil;
import javafx.concurrent.Task;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

public class CrawlingTask extends Task<String> {
    @Override
    protected String call() throws Exception {
        CrawlingData crawlingData = CrawlingController.getInstance().getCrawlingData();
        long startTimeMillis = System.currentTimeMillis();
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("www.yy11.com")
                .setPath("api/")
                .addParameter("m", "shop")
                .addParameter("c", "lists")
                .addParameter("v", "search")
                .addParameter("data", crawlingData.toString())
                .build();
        String result = HttpsUtil.sendGet(uri.toString());
        updateMessage(String.valueOf(System.currentTimeMillis() - startTimeMillis));
        return result;
    }
}
