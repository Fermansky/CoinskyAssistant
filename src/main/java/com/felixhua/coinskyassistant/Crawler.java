package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.GoodsItem;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.util.ConstantUtil;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler extends Thread {
    private static final int DEFAULT_SLEEP_TIME = 10000; // 10 seconds
    public static String url = "https://www.yy11.com/shop/show/index/id/2636.html";
    private static final String baseUrl = "https://www.yy11.com";
    private boolean isCrawling = false;
    private long startTime;
    public int averageCrawlTime = 0;
    private final MainController mainController;
    private GoodsItem lastItem;
    private int failureTempCount = 0;
    public int failureCount = 0;

    @Override
    public void run() {
        crawl();
//        crawlWithMultipleThread();
    }

    /**
     * Default crawl method. Old, easy and inefficient.
     */
    private void crawl() {
        try (WebClient webClient = new WebClient()) {
            WebClientOptions options = webClient.getOptions();
            options.setJavaScriptEnabled(true);
            options.setCssEnabled(false);
            options.setRedirectEnabled(true);

            failureTempCount = 0;
            while (true) {
                if (!isCrawling) {
                    startTime = System.currentTimeMillis();
                }
                isCrawling = true;
                HtmlPage page = webClient.getPage(url);
                int jsLeft = webClient.waitForBackgroundJavaScript(getTimeoutMillis());
                if (jsLeft > 0) {
                    LogUtil.log(LogLevel.WARNING, jsLeft + "项JS任务未完成，商品获取失败。");
                    failureTempCount += 1;
                    failureCount += 1;
                    continue;
                }
                failureTempCount = 0;
                GoodsItem latestItem = getLatestItem(page.asXml());
                if (latestItem == null) {
                    continue;
                }
                processLatestItem(latestItem);
                if(latestItem.getPrice().endsWith("0")){
                    if(mainController.getItemMapper().checkItem(latestItem.getItemUrl()) == 0) {
                        ItemPO itemPO = lastItem.convertToItemPO();
                        mainController.getItemMapper().insertItem(itemPO);
                        LogUtil.log(lastItem.getName() + " 已被加入数据库");
                    }
                }
                int delay = (int) (System.currentTimeMillis() - startTime);
                if (averageCrawlTime == 0) {
                    averageCrawlTime = delay;
                } else {
                    averageCrawlTime = (averageCrawlTime + delay) / 2;
                }
                mainController.getMessagePane().setDelayLabel(delay / 1000);
                int sleepTime = Math.max(DEFAULT_SLEEP_TIME - delay, 0);
                Thread.sleep(sleepTime);
                isCrawling = false;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Experimental. Will cause a lot of error, but it
     * should work anyway. However, this should be the
     * direction of developing.
     */
    private void crawlWithMultipleThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ArrayList<CrawlerThread> tasks = new ArrayList<>();
        tasks.add(new CrawlerThread(this));
        tasks.add(new CrawlerThread(this));
        tasks.add(new CrawlerThread(this));
        while (true) {
            try {
                if (!isCrawling) {
                    startTime = System.currentTimeMillis();
                }
                isCrawling = true;
                String s = executorService.invokeAny(tasks);
                if (s.equals("n")) {
                    LogUtil.log(LogLevel.WARNING,"商品获取失败。");
                    failureCount += 1;
                    continue;
                }
                GoodsItem latestItem = getLatestItem(s);
                processLatestItem(latestItem);
                int delay = (int) (System.currentTimeMillis() - startTime);
                if (averageCrawlTime == 0) {
                    averageCrawlTime = delay;
                } else {
                    averageCrawlTime = (averageCrawlTime + delay) / 2;
                }
                mainController.getMessagePane().setDelayLabel(delay / 1000);
                isCrawling = false;
                Thread.sleep(10000);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void updateItemsInfo() {
        long start = System.currentTimeMillis();
//        List<ItemPO> unprocessedItems = mainController.getItemMapper().selectItemsWithoutDescription();
        List<ItemPO> unprocessedItems = mainController.getItemMapper().selectItemsWithoutImg();
//        mainController.showInfo("更新数据库中 待更新条目：" + unprocessedItems.size());
        try(WebClient webClient = new WebClient()) {
            WebClientOptions options = webClient.getOptions();
            options.setJavaScriptEnabled(true);
            options.setCssEnabled(false);
            options.setRedirectEnabled(true);

            for(ItemPO itemPO : unprocessedItems){
                System.out.println("更新数据库中：" + unprocessedItems.indexOf(itemPO)+1 + "/" + unprocessedItems.size());
                long tempStart = System.currentTimeMillis();
                HtmlPage page = webClient.getPage(itemPO.getUrl());
                webClient.waitForBackgroundJavaScript(10000);
                Document parse = Jsoup.parse(page.asXml());

                String name = parse.getElementsByClass("goodsTitle").get(0).text();
                name = name.substring(name.indexOf("]") + 1);
                String price = parse.getElementsByClass("quotePrice").get(0).text();
                price = price.substring(2);
                price = price.split("\\.")[0];
                String intro = parse.getElementById("intro").text().substring(6);
                int lastSpaceIndex = intro.lastIndexOf(" ");
                intro = intro.substring(0, lastSpaceIndex);
                String goodsTime = parse.getElementsByClass("goodsTime").get(0).text().substring(6);
                lastSpaceIndex = goodsTime.lastIndexOf(" ");
                goodsTime = goodsTime.substring(0, lastSpaceIndex);

                String img = parse.getElementById("imgsShower").children().get(0).attr("src");
                downloadImage(img);

                itemPO.setDescription(intro);
                itemPO.setCreateTime(goodsTime);
                itemPO.setName(name);
                itemPO.setPrice(Integer.parseInt(price));
                itemPO.setImgUrl(img);
                mainController.getItemMapper().updateItem(itemPO);

                if(System.currentTimeMillis() - tempStart <= 3000) {
                    Thread.sleep(2000);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        mainController.showInfo("更新数据库完成，共更新" + unprocessedItems.size() + "条记录，耗时" + ((end - start)/1000) + "秒。");
    }

    public void downloadImage(String imageUrl) throws IOException {
        String fileName = ConstantUtil.LOCAL_IMAGE_STORAGE + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        File outputFile = new File(fileName);

        if (outputFile.exists()) {
            return;
        }

        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream();
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void processLatestItem(GoodsItem latestItem) {
        if (lastItem == null || !lastItem.getName().equals(latestItem.getName())) {
            updateItem(latestItem);
            LogUtil.log("新上架货品 " + latestItem + " 。");
            lastItem = latestItem;
            if (latestItem.getStatus().equals("已售")) {
                mainController.getMessagePane().itemSold();
            }
        } else if (latestItem.getStatus().equals("已售") && lastItem.getStatus().equals("待售")) {
            mainController.getMessagePane().itemSold();
            lastItem = latestItem;
            LogUtil.log("商品 " + latestItem.getName() + " 已售出。");
            VoiceUtil.play(VoicePrompt.ITEM_SOLD);
        } else {
            LogUtil.log("无新上架商品。");
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
        if (Jsoup.parse(html).head().select("title").text().equals("当前操作错误提示")) {
            LogUtil.log("访问速度过快，访问被拒绝。");
            return null;
        }

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
            LogUtil.log(LogLevel.WARNING, "预期之外的元素数量，预期值为1，实际读到" + elements.size() + " 。");
        }
        if (elements.first() == null) {
            LogUtil.log(LogLevel.WARNING, "读到null");
            return null;
        }
        return Objects.requireNonNull(elements.first()).text();
    }

    public Crawler(MainController controller) {
        this.mainController = controller;
        controller.setCrawler(this);
    }
}
