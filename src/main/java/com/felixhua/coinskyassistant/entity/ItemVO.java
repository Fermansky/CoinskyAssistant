package com.felixhua.coinskyassistant.entity;

import javafx.scene.image.Image;
import lombok.Data;

import java.util.Objects;

@Data
public class ItemVO {
    private String imgUrl;
    private String name;
    /**
     * 0 - 待售; 2 - 已售
     */
    private int status;
    private String formattedPrice;
    private String time;

    public Image getImage() {
        return new Image(imgUrl);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ItemVO itemVO)) {
            return false;
        }
        return status == itemVO.status
                && Objects.equals(imgUrl, itemVO.imgUrl)
                && Objects.equals(name, itemVO.name)
                && Objects.equals(formattedPrice, itemVO.formattedPrice)
                && Objects.equals(time, itemVO.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgUrl, name, status, formattedPrice);
    }
}
