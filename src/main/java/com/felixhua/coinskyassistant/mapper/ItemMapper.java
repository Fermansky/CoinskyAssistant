package com.felixhua.coinskyassistant.mapper;

import com.felixhua.coinskyassistant.entity.ItemPO;

import java.util.List;

public interface ItemMapper {
    List<ItemPO> selectAllItems();
    List<ItemPO> selectItemsWithoutDescription();
    List<ItemPO> selectItemsWithoutImg();
    int updateItem(ItemPO itemPO);
    int checkItem(String url);
    void insertItem(ItemPO item);
}
