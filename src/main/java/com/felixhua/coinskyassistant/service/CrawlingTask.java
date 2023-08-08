package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.controller.CrawlingController;
import com.felixhua.coinskyassistant.entity.CrawlingData;
import com.felixhua.coinskyassistant.util.HttpsUtil;
import javafx.concurrent.Task;

public class CrawlingTask extends Task<String> {
    @Override
    protected String call() throws Exception {
        CrawlingData crawlingData = CrawlingController.getInstance().getCrawlingData();
        long startTimeMillis = System.currentTimeMillis();
        String result = HttpsUtil.sendGet(crawlingData);
        updateMessage(String.valueOf(System.currentTimeMillis() - startTimeMillis));
        return result;
    }
}
