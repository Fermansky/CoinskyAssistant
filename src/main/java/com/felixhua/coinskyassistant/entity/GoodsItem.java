package com.felixhua.coinskyassistant.entity;

import javafx.scene.image.Image;

public class GoodsItem {
    private String imgUrl;
    private String price;
    private String status;
    private String name;
    private String time;
    private String views;
    private String itemUrl;

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public Image getImage() {
        return new Image(imgUrl);
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getViews() {
        return views;
    }

    @Override
    public String toString() {
        return name;
    }
}
