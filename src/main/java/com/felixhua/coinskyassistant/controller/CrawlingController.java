package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.HttpsClient;
import com.felixhua.coinskyassistant.constants.Constant;
import com.felixhua.coinskyassistant.entity.ItemDTO;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.JQueryResultPO;
import com.felixhua.coinskyassistant.service.CrawlingScheduledService;
import com.felixhua.coinskyassistant.service.CrawlingService;
import com.felixhua.coinskyassistant.util.HtmlUtil;
import com.felixhua.coinskyassistant.util.LogUtil;
import javafx.concurrent.Worker;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CrawlingController {
    private long averageCrawlingTime = 0;
    private int failureCount = 0;
    private int successCount = 0;
    private static final CrawlingController instance = new CrawlingController();
    private MainController mainController;
    private CrawlingScheduledService crawlingScheduledService;
    private CrawlingService crawlingService;
    /**
     * for test only
     */
    private int testCount = 0;

    public static CrawlingController getInstance() {
        return instance;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void startCrawling() {
        this.crawlingScheduledService.start();
    }
    public long getAverageCrawlingTime() {
        return averageCrawlingTime;
    }
    public int getFailureCount() {
        return failureCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void startTestCrawlingService() {
        if(crawlingService.getState().equals(Worker.State.READY)) {
            crawlingService.start();
        } else {
            crawlingService.restart();
        }
    }

    private void initCrawlingService(){
        this.crawlingScheduledService = new CrawlingScheduledService();
        this.crawlingService = new CrawlingService();
        this.crawlingService.start();

        crawlingService.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                JQueryResultPO jQueryResultPO = HtmlUtil.parseJson(newValue);
                ItemDTO itemDTO = jQueryResultPO.getLatestItemDTO();
                mainController.getMessagePane().updateItem(itemDTO.convertToItemVO());
                successCount ++;
            }
        }));
        crawlingService.messageProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
//                System.out.println(newValue);
                int crawlingTimeMillis = Integer.parseInt(newValue);
                mainController.getMessagePane().setDelayLabel(crawlingTimeMillis);
                if(averageCrawlingTime == 0) {
                    averageCrawlingTime = crawlingTimeMillis;
                } else {
                    averageCrawlingTime = (averageCrawlingTime + crawlingTimeMillis) / 2;
                }
            }
        }));

        crawlingScheduledService.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                successCount ++;
            }
        }));
        crawlingScheduledService.lastValueProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == null) {
                failureCount ++;
                return;
            }
            mainController.getMessagePane().processLatestItem(newValue);
            if(newValue.getPrice().endsWith("0")){
                if(mainController.getItemMapper().checkItem(newValue.getItemUrl()) == 0) {
                    ItemPO itemPO = newValue.convertToItemPO();
                    mainController.getItemMapper().insertItem(itemPO);
                    LogUtil.log(newValue.getName() + " 已被加入数据库");
                }
            }
            successCount ++;
        }));
        crawlingScheduledService.messageProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                long crawlingTimeMillis = Long.parseLong(newValue);
                mainController.getMessagePane().setDelayLabel((int) (crawlingTimeMillis / 1000));
                if(averageCrawlingTime == 0) {
                    averageCrawlingTime = crawlingTimeMillis;
                } else {
                    averageCrawlingTime = (averageCrawlingTime + crawlingTimeMillis) / 2;
                }
            }
        });
    }
    private CrawlingController() {
        initCrawlingService();
//        startCrawling();
    }
}
