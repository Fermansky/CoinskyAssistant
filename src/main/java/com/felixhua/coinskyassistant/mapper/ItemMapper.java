package com.felixhua.coinskyassistant.mapper;

import com.felixhua.coinskyassistant.entity.ImagePO;
import com.felixhua.coinskyassistant.entity.ItemPO;

import java.util.List;

public interface ItemMapper {
    List<ItemPO> selectAllItems();
    List<ItemPO> selectItemsWithoutDescription();
    List<ItemPO> selectItemsWithoutImg();
    List<ItemPO> selectItemsWithIncompleteProperties();
    int updateItem(ItemPO itemPO);
    int checkItem(String url);
    int getIdByUrl(String url);
    void insertItem(ItemPO item);
    void insertImage(ImagePO imagePO);
}
