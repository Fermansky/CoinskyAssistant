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

public class UpdateService extends Service<List<ItemPO>> {
    private List<ItemPO> itemPOList;

    public void setItemPOList(List<ItemPO> itemPOList) {
        this.itemPOList = itemPOList;
    }

    @Override
    protected Task<List<ItemPO>> createTask() {
        return new Task<>() {
            @Override
            protected List<ItemPO> call() throws Exception {
                try (WebClient webClient = new WebClient()) {
                    WebClientOptions options = webClient.getOptions();
                    options.setJavaScriptEnabled(true);
                    options.setCssEnabled(false);
                    options.setRedirectEnabled(true);

                    for (ItemPO itemPO : itemPOList) {
                        updateMessage("更新数据库中：" + (itemPOList.indexOf(itemPO) + 1) + "/" + itemPOList.size());
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

                        itemPO.setDescription(intro);
                        itemPO.setCreateTime(goodsTime);
                        itemPO.setName(name);
                        itemPO.setPrice(Integer.parseInt(price));
                        itemPO.setImgUrl(img);

                        if (System.currentTimeMillis() - tempStart <= 3000) {
                            Thread.sleep(2000);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return itemPOList;
            }
        };
    }
}
