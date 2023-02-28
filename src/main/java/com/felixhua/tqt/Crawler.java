package com.felixhua.tqt;

import com.felixhua.tqt.controller.MainController;
import com.felixhua.tqt.entity.GoodsItem;
import com.felixhua.tqt.util.SoundUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Crawler extends Thread {
    private static final int DEFAULT_SLEEP_TIME = 10000; // 10 seconds
    private static String url = "https://www.yy11.com/shop/show/index/id/2636.html";
    private static String baseUrl = "https://www.yy11.com";
    private static AudioClip newItemSound = new AudioClip(Crawler.class.getResource("/sound/new_item.mp3").toExternalForm());
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

            try {
                while (true) {
                    long beginTime = System.currentTimeMillis();
                    HtmlPage page = webClient.getPage(url);
                    int jsLeft = webClient.waitForBackgroundJavaScript(getTimeoutMillis());
                    if (jsLeft > 0) {
                        mainController.log(jsLeft + " tasks remain to be done.");
                        failureTempCount += 1;
                        continue;
                    }
                    failureTempCount = 0;
                    GoodsItem latestItem = getLatestItem(page.asXml());
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
                        SoundUtil.play("item_sold.mp3");
                    } else {
                        mainController.log("无新上架货品");
                    }
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

    private int getTimeoutMillis() {
        return 10000 + failureTempCount*5000;
    }

    private void updateItem(GoodsItem item) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!mainController.getMessagePane().isReady()) {
            if (date.equals(item.getTime().split(" ")[0])) {
                if (item.getPrice().endsWith("议价")) {
                    SoundUtil.play("end_of_day.mp3");
                }
                else SoundUtil.play("started.mp3");
            }
            else SoundUtil.play("not_started.mp3");
        } else {
            if (item.getPrice().endsWith("议价")) {
                SoundUtil.play("end_of_day.mp3");
            }
            else SoundUtil.play("new_item.mp3");
        }
        mainController.getMessagePane().updateItem(item);
    }

    private GoodsItem getLatestItem(String html) {
        GoodsItem goodsItem = new GoodsItem();
//        System.out.println(html);
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
