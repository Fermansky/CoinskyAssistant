package com.felixhua.coinskyassistant.entity;

import com.felixhua.coinskyassistant.constants.Constant;
import javafx.scene.image.Image;
import lombok.Data;

import java.util.Objects;

@Data
public class ItemVO {
    private int id;
    private String imgUrl;
    private String name;
    /**
     * 0 - 待售; 2 - 已售
     */
    private int status;
    private String formattedPrice;
    private String time;

    public Image getThumbnailImage() {
        return new Image(imgUrl + "/s");
    }
    public String getUrl() {
        return Constant.COINSKY_SHOP_VIEW_URL + id + ".html";
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
        return Objects.hash(id, imgUrl, name, status, formattedPrice);
    }
}
