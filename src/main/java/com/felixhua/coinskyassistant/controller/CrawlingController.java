package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.entity.ItemDTO;
import com.felixhua.coinskyassistant.entity.JQueryResultPO;
import com.felixhua.coinskyassistant.service.CrawlingService;
import com.felixhua.coinskyassistant.util.ConvertUtil;
import com.felixhua.coinskyassistant.util.HtmlUtil;
import javafx.concurrent.Worker;

public class CrawlingController {
    private long averageCrawlingTime = 0;
    private int successCount = 0;
    private ItemDTO latestItemDTO;
    private static final CrawlingController instance = new CrawlingController();
    private MainController mainController;
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
        this.crawlingService.start();
    }
    public long getAverageCrawlingTime() {
        return averageCrawlingTime;
    }
    public int getFailureCount() {
        return 0;
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
        this.crawlingService = new CrawlingService();

        crawlingService.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                JQueryResultPO jQueryResultPO = HtmlUtil.parseJson(newValue);
                ItemDTO itemDTO = jQueryResultPO.getLatestItemDTO();
                successCount ++;
                if (latestItemDTO != null) {    // 最新商品不为空
                    if (latestItemDTO.getId() == itemDTO.getId()) { // 商品id相同，属于同一件商品
                        if (latestItemDTO.getImgUrls().equals(itemDTO.getImgUrls())
                        && latestItemDTO.getName().equals(itemDTO.getName())
                        && latestItemDTO.getStatus() == itemDTO.getStatus()
                        && latestItemDTO.getPrice() == itemDTO.getPrice()
                        && latestItemDTO.getDescription().equals(itemDTO.getDescription())) {   // 无需更新商品
                            return ;
                        }
                    }
                }
                // 更新商品
                mainController.getMessagePane().updateItem(ConvertUtil.convertToItemVO(itemDTO));
                mainController.updateAndInsertItem(ConvertUtil.convertToItemPO(itemDTO));
                latestItemDTO = itemDTO;
            }
        }));
        crawlingService.messageProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                int crawlingTimeMillis = Integer.parseInt(newValue);
                mainController.getMessagePane().updateDelayLabel(crawlingTimeMillis);
                if(averageCrawlingTime == 0) {
                    averageCrawlingTime = crawlingTimeMillis;
                } else {
                    averageCrawlingTime = (averageCrawlingTime + crawlingTimeMillis) / 2;
                }
            }
        }));
    }
    private CrawlingController() {
        initCrawlingService();
        startCrawling();
    }
}
