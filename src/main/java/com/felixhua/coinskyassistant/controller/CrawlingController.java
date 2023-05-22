package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.service.CrawlingScheduledService;
import com.felixhua.coinskyassistant.util.LogUtil;

public class CrawlingController {
    private static final CrawlingController instance = new CrawlingController();
    private MainController mainController;
    private CrawlingScheduledService crawlingScheduledService;
    public static CrawlingController getInstance() {
        return instance;
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void startCrawling() {
        this.crawlingScheduledService.start();
    }
    private void initCrawlingService(){
        this.crawlingScheduledService = new CrawlingScheduledService();
        crawlingScheduledService.lastValueProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == null) {
                return;
            }
            System.out.println("lastValueProperty " + newValue);
            mainController.getMessagePane().processLatestItem(newValue);
            if(newValue.getPrice().endsWith("0")){
                if(mainController.getItemMapper().checkItem(newValue.getItemUrl()) == 0) {
                    ItemPO itemPO = newValue.convertToItemPO();
                    mainController.getItemMapper().insertItem(itemPO);
                    LogUtil.log(newValue.getName() + " 已被加入数据库");
                }
            }
        }));
        crawlingScheduledService.messageProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                mainController.getMessagePane().setDelayLabel(Integer.parseInt(newValue));
            }
        });
    }
    private CrawlingController() {
        initCrawlingService();
        startCrawling();
    }
}
