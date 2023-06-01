package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.constants.Constant;
import com.felixhua.coinskyassistant.entity.GoodsItem;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.felixhua.coinskyassistant.util.HtmlUtil;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * Depreciated. Use CrawlingService instead.
 */
public class CrawlingScheduledService extends ScheduledService<GoodsItem> {
    private static WebClient webClient;
    private int failureCount;

    @Override
    protected Task<GoodsItem> createTask() {
        return new CrawlingTask();
    }

    public CrawlingScheduledService() {
        webClient = new WebClient();
        WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled(true);
        options.setCssEnabled(false);
        options.setRedirectEnabled(true);

        this.setPeriod(Duration.seconds(10));
        this.setRestartOnFailure(true);
    }

    protected int getTimeoutMillis() {
        return 10000 + 5000 * failureCount;
    }

    class CrawlingTask extends Task<GoodsItem> {
        @Override
        protected GoodsItem call() throws Exception {
            long startTime = System.currentTimeMillis();
            HtmlPage page = webClient.getPage(Constant.TAO_URL);
            int jsLeft = webClient.waitForBackgroundJavaScript(getTimeoutMillis());
            if (jsLeft > 0) {
                LogUtil.log(LogLevel.WARNING, jsLeft + "项JS任务未完成，商品获取失败。");
                failureCount += 1;
                return null;
            }
            GoodsItem latestItem = HtmlUtil.getLatestItem(page.asXml());
            if (latestItem == null) {
                return null;
            }
            failureCount = 0;
            updateMessage(String.valueOf(System.currentTimeMillis() - startTime));
            return latestItem;
        }
    }
}
