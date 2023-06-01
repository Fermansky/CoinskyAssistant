package com.felixhua.coinskyassistant.util;

import com.felixhua.coinskyassistant.constants.Constant;
import com.felixhua.coinskyassistant.entity.ImagePO;
import com.felixhua.coinskyassistant.entity.ItemDTO;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.ItemVO;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertUtil {
    public static ItemVO convertToItemVO(ItemDTO itemDTO) {
        ItemVO itemVO = new ItemVO();
        itemVO.setId(itemDTO.getId());
        itemVO.setStatus(itemDTO.getStatus());
        itemVO.setName(itemDTO.getName());
        itemVO.setImgUrl(itemDTO.getImgUrls().get(0));
        if(itemDTO.getPrice() == 0) {
            itemVO.setFormattedPrice("¥议价");
        } else {
            itemVO.setFormattedPrice(String.format("¥%.2f", itemDTO.getPrice()));
        }
        itemVO.setTime(itemDTO.getCreateTime());
        return itemVO;
    }

    public static ItemPO convertToItemPO(ItemDTO itemDTO) {
        ItemPO itemPO = new ItemPO();
        itemPO.setName(itemDTO.getName());
        itemPO.setPrice((int) itemDTO.getPrice());
        itemPO.setId(itemDTO.getId());
        itemPO.setDescription(itemDTO.getDescription());
        itemPO.setCreateTime(itemDTO.getCreateTime());
        itemPO.setUrl(Constant.COINSKY_SHOP_VIEW_URL + itemDTO.getId() + ".html");
        itemPO.setImagePOS(convertToImagePOS(itemDTO.getId(), itemDTO.getImgUrls()));
        itemPO.setImgUrl(itemDTO.getImgUrls().get(0));
        return itemPO;
    }

    private static List<ImagePO> convertToImagePOS(int itemId, List<String> imgUrls) {
        return imgUrls.stream()
                .map(imgUrl -> new ImagePO(itemId, imgUrl))
                .collect(Collectors.toList());
    }
}
