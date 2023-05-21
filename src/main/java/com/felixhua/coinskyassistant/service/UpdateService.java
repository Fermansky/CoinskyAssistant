package com.felixhua.coinskyassistant.service;

import com.felixhua.coinskyassistant.entity.ItemPO;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This Service receives a list of ItemPO (url is required), and returns them with full information
 */
public class UpdateService extends Service<List<ItemPO>> {
    private List<ItemPO> itemPOList;

    public void setItemPOList(List<ItemPO> itemPOList) {
        this.itemPOList = itemPOList;
    }

    @Override
    protected Task<List<ItemPO>> createTask() {
        return new Task<>() {
            @Override
            protected List<ItemPO> call() {
                try (WebClient webClient = new WebClient()) {
                    WebClientOptions options = webClient.getOptions();
                    options.setJavaScriptEnabled(true);
                    options.setCssEnabled(false);
                    options.setRedirectEnabled(true);

                    for (ItemPO itemPO : itemPOList) {
                        long tempStart = System.currentTimeMillis();
                        updateMessage("获取数据中：" + (itemPOList.indexOf(itemPO) + 1) + "/" + itemPOList.size());
                        HtmlPage page = webClient.getPage(itemPO.getUrl());
                        webClient.waitForBackgroundJavaScript(10000);
                        Document parse = Jsoup.parse(page.asXml());

                        String name = parse.getElementsByClass("goodsTitle").get(0).text();
                        name = name.substring(name.indexOf("]") + 1);
                        itemPO.setName(name);

                        String price = parse.getElementsByClass("quotePrice").get(0).text();
                        price = price.substring(2);
                        price = price.split("\\.")[0];
                        itemPO.setPrice(Integer.parseInt(price));

                        String intro = Objects.requireNonNull(parse.getElementById("intro")).text().substring(6);
                        int lastSpaceIndex = intro.lastIndexOf(" ");
                        intro = intro.substring(0, lastSpaceIndex);
                        itemPO.setDescription(intro);

                        String goodsTime = parse.getElementsByClass("goodsTime").get(0).text().substring(6);
                        lastSpaceIndex = goodsTime.lastIndexOf(" ");
                        goodsTime = goodsTime.substring(0, lastSpaceIndex);
                        itemPO.setCreateTime(goodsTime);

                        String img = Objects.requireNonNull(parse.getElementById("imgsShower")).children().get(0).attr("src");
                        itemPO.setImgUrl(img);

                        if (System.currentTimeMillis() - tempStart <= 3000) {
                            Thread.sleep(2000);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                updateMessage("数据获取完成，正在更新数据库……");
                return itemPOList;
            }
        };
    }
}
