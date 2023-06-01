package com.felixhua.coinskyassistant.entity;

import lombok.Data;

import java.util.List;

@Data
public class ItemDTO {
    int id;
    List<String> imgUrls;
    String name;
    /**
     * 0 - 待售; 2 - 已售
     */
    int status;
    double price;
    String createTime;
    int view;
    String description;
}
