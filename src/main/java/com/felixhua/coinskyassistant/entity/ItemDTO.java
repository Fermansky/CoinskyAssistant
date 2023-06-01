package com.felixhua.coinskyassistant.entity;

import lombok.Data;

@Data
public class ItemDTO {
    int id;
    String imgUrl;
    String name;
    /**
     * 0 - 待售; 2 - 已售
     */
    int status;
    double price;
    String time;
    int view;
    String description;

    public ItemVO convertToItemVO() {
        ItemVO itemVO = new ItemVO();
        itemVO.setStatus(status);
        itemVO.setName(name);
        itemVO.setImgUrl(imgUrl);
        if(price == 0) {
            itemVO.setFormattedPrice("¥议价");
        } else {
            itemVO.setFormattedPrice(String.format("¥%.2f", price));
        }
        itemVO.setTime(time);
        return itemVO;
    }
}
