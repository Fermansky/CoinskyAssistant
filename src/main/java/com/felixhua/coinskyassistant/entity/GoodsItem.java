package com.felixhua.coinskyassistant.entity;

import javafx.scene.image.Image;
import lombok.Data;

@Data
public class GoodsItem {
    private String imgUrl;
    private String price;
    private String status;
    private String name;
    private String time;
    private String views;
    private String itemUrl;

    public Image getImage() {
        return new Image(imgUrl);
    }

    public ItemPO convertToItemPO() {
        ItemPO itemPO = new ItemPO();
        itemPO.setName(this.name);
        String parsePrice = price.substring(1);
        parsePrice = parsePrice.split("\\.")[0];
        itemPO.setPrice(Integer.parseInt(parsePrice));
        itemPO.setUrl(this.itemUrl);
        return itemPO;
    }

}
