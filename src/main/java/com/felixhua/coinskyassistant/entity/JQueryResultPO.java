package com.felixhua.coinskyassistant.entity;

import com.felixhua.coinskyassistant.util.HtmlUtil;
import com.felixhua.coinskyassistant.util.StringUtil;
import lombok.Data;

import java.util.List;

@Data
public class JQueryResultPO {
    int code;
    List<List<String>> data;

    public ItemDTO getItemDTO(int index) {
        if (index >= data.size()) {
            System.err.printf("尝试获取超出data范围的元素，获取index：%d, data范围：%d", index, data.size());
            return null;
        }
        List<String> element = data.get(index);
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(Integer.parseInt(element.get(0)));
        String hash = element.get(1);
        itemDTO.setImgUrls(StringUtil.divideImgHash(hash));
        itemDTO.setName(element.get(2));
        itemDTO.setStatus(Integer.parseInt(element.get(3)));
        itemDTO.setPrice(Double.parseDouble(element.get(4)));
        long epochSecond = Long.parseLong(element.get(5));
        itemDTO.setCreateTime(HtmlUtil.getFormattedTime(epochSecond));
        itemDTO.setView(Integer.parseInt(element.get(6)));
        itemDTO.setDescription(element.get(10));
        return itemDTO;
    }

    public ItemDTO getLatestItemDTO() {
        return getItemDTO(0);
    }
}
