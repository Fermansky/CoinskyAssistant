package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.CrawlingData;
import com.felixhua.coinskyassistant.entity.ItemDTO;
import com.felixhua.coinskyassistant.entity.JQueryResult;
import com.felixhua.coinskyassistant.util.ConvertUtil;
import com.felixhua.coinskyassistant.util.HtmlUtil;
import com.felixhua.coinskyassistant.util.HttpsUtil;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;

public class CrawlingService extends ScheduledService<String> {
    public CrawlingService() {
        this.setPeriod(Duration.seconds(2));
        this.setRestartOnFailure(true);
    }

    @Override
    protected Task<String> createTask() {
        return new CrawlingTask();
    }

    /**
     * 通过钱币天堂后端api爬取最近的商品条目并更新数据库
     * @param amount 需要爬取的商品数量
     * @return 爬取到的最早发布的一条商品数据
     */
    public static ItemDTO crawlAndUpdate(int amount) {
        CrawlingData crawlingData = new CrawlingData(2636, amount, 0);
        ItemDTO itemDTO = null;
        try {
            String result = HttpsUtil.sendGet(crawlingData);
            JQueryResult jQueryResult = HtmlUtil.parseJson(result);
            int size = jQueryResult.getData().size();
            for(int i = 0; i < size; i++) {
                itemDTO = jQueryResult.getItemDTO(i);
                MainController.getInstance().updateAndInsertItem(ConvertUtil.convertToItemPO(itemDTO));
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return itemDTO;
    }
}
