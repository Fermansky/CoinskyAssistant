package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.GoodsItem;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Crawler extends Thread {
    private static final int DEFAULT_SLEEP_TIME = 10000; // 10 seconds
    private static String url = "https://www.yy11.com/shop/show/index/id/2636.html";
    private static String baseUrl = "https://www.yy11.com";
    private MainController mainController;
    private GoodsItem lastItem;
    private int failureTempCount = 0;

    @Override
    public void run() {
        try (WebClient webClient = new WebClient()) {
            WebClientOptions options = webClient.getOptions();
            options.setJavaScriptEnabled(true);
            options.setCssEnabled(false);
            options.setRedirectEnabled(true);

            failureTempCount = 0;
//            ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
//            service.scheduleWithFixedDelay(() -> {
//                long beginTime = System.currentTimeMillis();
//                HtmlPage page = null;
//                try {
//                    page = webClient.getPage(url);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                int jsLeft = webClient.waitForBackgroundJavaScript(getTimeoutMillis());
//                if (jsLeft > 0) {
//                    mainController.log(jsLeft + " tasks remain to be done.");
//                    failureTempCount += 1;
//                    return ;
//                }
//                failureTempCount = 0;
//                GoodsItem latestItem = getLatestItem(page.asXml());
//                processLatestItem(latestItem);
//                int delay = (int) (System.currentTimeMillis() - beginTime);
//                System.out.println("EOT");
////                mainController.getMessagePane().setDelayLabel(delay / 1000);
//            }, 1, 10, TimeUnit.SECONDS);
            try {
                while (true) {
                    long beginTime = System.currentTimeMillis();
                    HtmlPage page = webClient.getPage(url);
                    int jsLeft = webClient.waitForBackgroundJavaScript(getTimeoutMillis());
//                    int jsLeft = webClient.waitForBackgroundJavaScript(1000);
                    if (jsLeft > 0) {
                        mainController.log(jsLeft + " tasks remain to be done.");
                        failureTempCount += 1;
                        continue;
                    }
                    failureTempCount = 0;
                    GoodsItem latestItem = getLatestItem(page.asXml());
                    processLatestItem(latestItem);
                    int delay = (int) (System.currentTimeMillis() - beginTime);
                    mainController.getMessagePane().setDelayLabel(delay / 1000);
                    int sleepTime = Math.max(DEFAULT_SLEEP_TIME - delay, 0);
                    Thread.sleep(sleepTime);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processLatestItem(GoodsItem latestItem) {
        if (lastItem == null || !lastItem.getName().equals(latestItem.getName())) {
            updateItem(latestItem);
            mainController.log("新上架" + latestItem);
            lastItem = latestItem;
            if (latestItem.getStatus().equals("已售")) {
                mainController.getMessagePane().itemSold();
            }
        } else if (latestItem.getStatus().equals("已售") && lastItem.getStatus().equals("待售")) {
            mainController.getMessagePane().itemSold();
            lastItem = latestItem;
            mainController.log("商品已售出");
            VoiceUtil.play(VoicePrompt.ITEM_SOLD);
        } else {
            mainController.log("无新上架货品");
        }
    }

    private int getTimeoutMillis() {
        return 10000 + failureTempCount*5000;
    }

    private void updateItem(GoodsItem item) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!mainController.getMessagePane().isReady()) {
            if (date.equals(item.getTime().split(" ")[0])) {
                if (item.getPrice().endsWith("议价")) {
                    VoiceUtil.play(VoicePrompt.END_OF_DAY);
                }
                else VoiceUtil.play(VoicePrompt.STARTED);
            }
            else VoiceUtil.play(VoicePrompt.NOT_STARTED);
        } else {
            if (item.getPrice().endsWith("议价")) {
                VoiceUtil.play(VoicePrompt.END_OF_DAY);
            }
            else VoiceUtil.play(VoicePrompt.NEW_ITEM);
        }
        mainController.getMessagePane().updateItem(item);
    }

    private GoodsItem getLatestItem(String html) {
        GoodsItem goodsItem = new GoodsItem();
        try {
            Element first = Jsoup.parse(html).body().select(".goodsitem").get(0);
            if (first == null)  return null;
            goodsItem.setItemUrl(baseUrl + first.select("a").first().attr("href"));
            String imgUrl = first.select(".itemFace").first().select("img").first().attr("src");
            goodsItem.setImgUrl(imgUrl);
            goodsItem.setPrice(getTextByClass(first, "itemPrice").split(" ")[0]);
            goodsItem.setStatus(getTextByClass(first, "itemPrice").split(" ")[1]);
            goodsItem.setName(getTextByClass(first, "ItemName"));
            goodsItem.setTime(getTextByClass(first, "itemTime").substring(0, 16));
            goodsItem.setViews(getTextByClass(first, "itemTime").split(" ")[2]);
        } catch (Exception e) {
            System.out.println(html);
            e.printStackTrace();
        }
        return goodsItem;
    }

    private String getTextByClass(Element element, String className) {
        Elements elements = element.select("." + className);
        if (elements.size() != 1) {
            mainController.log("Unexpected Elements' Size, expected 1, read " + elements.size());
        }
        if (elements.first() == null) {
            mainController.log("CrawlerUtils.getTextByClass: read null");
            return null;
        }
        return Objects.requireNonNull(elements.first()).text();
    }

    public Crawler(MainController controller) {
        this.mainController = controller;
        controller.setCrawler(this);
    }
}
