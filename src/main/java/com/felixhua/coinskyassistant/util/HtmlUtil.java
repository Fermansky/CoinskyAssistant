package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.constants.Constant;
import com.felixhua.coinskyassistant.entity.GoodsItem;
import com.felixhua.coinskyassistant.entity.JQueryResultPO;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HtmlUtil {
    static Gson gson = new Gson();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static JQueryResultPO parseJson(String json) {
        //        System.out.println(jQueryResultPO.getItemDTO(0));
        return gson.fromJson(json, JQueryResultPO.class);
    }

    public static String getFormattedTime(long epochSecond) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

    /**
     * Depreciated.
     * @param html
     * @return
     */
    public static GoodsItem getLatestItem(String html) {
        if (Jsoup.parse(html).head().select("title").text().equals("当前操作错误提示")) {
            LogUtil.log("访问速度过快，访问被拒绝。");
            return null;
        }

        GoodsItem goodsItem = new GoodsItem();
        try {
            Element first = Jsoup.parse(html).body().select(".goodsitem").get(0);
            if (first == null)  return null;
            goodsItem.setItemUrl(Constant.COINSKY_URL + first.select("a").first().attr("href"));
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

    private static String getTextByClass(Element element, String className) {
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
}
