package com.felixhua.coinskyassistant.entity;

import lombok.Data;

import java.util.List;

@Data
public class ItemPO {
    private int id;
    private String name;
    private String description;
    private int price;
    private String url;
    private String createTime;
    private String imgUrl;
    private List<ImagePO> imagePOS;
}
