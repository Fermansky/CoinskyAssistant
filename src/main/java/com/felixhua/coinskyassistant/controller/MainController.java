package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.mapper.ItemMapper;
import com.felixhua.coinskyassistant.service.UpdateService;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import com.felixhua.coinskyassistant.util.ConstantUtil;
import com.felixhua.coinskyassistant.util.DownloadUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.util.List;

public class MainController {
    private static final MainController mainController = new MainController();
    public MessagePane messagePane;
    public SettingStage settingStage;
    public ObjectProperty<VoiceAssistant> voiceAssistantProperty = new SimpleObjectProperty<>();
    private SqlSessionFactory sqlSessionFactory;
    private ItemMapper itemMapper;
    private UpdateService updateService;
    private CrawlingController crawlingController;

    public static MainController getInstance() {
        return mainController;
    }

    public void updateInfo() {
        List<ItemPO> unprocessedItems = getItemMapper().selectItemsWithIncompleteProperties();
        updateService.setItemPOList(unprocessedItems);
        if(updateService.getState().equals(Worker.State.READY)) {
            updateService.start();
        } else {
            updateService.restart();
        }
    }

    public void showInfo(String info) {
        settingStage.getBottomInfo().setText(info);
    }


    public void setMessagePane(MessagePane messagePane) {
        this.messagePane = messagePane;
    }

    public void setSettingStage(SettingStage settingStage) {
        this.settingStage = settingStage;
    }
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }
    public void setItemMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public MessagePane getMessagePane() {
        return messagePane;
    }

    public SettingStage getSettingStage() {
        return settingStage;
    }

    public VoiceAssistant getVoiceAssistant() {
        return voiceAssistantProperty.get();
    }

    public ItemMapper getItemMapper() {
        return itemMapper;
    }

    public CrawlingController getCrawlingController() {
        return crawlingController;
    }

    private void initUpdateService() {
        this.updateService = new UpdateService();
        updateService.messageProperty().addListener((observable, oldValue, newValue) -> {
            showInfo(newValue);
        });
        updateService.setOnSucceeded(event -> {
            List<ItemPO> itemPOS = updateService.getValue();
            for(ItemPO itemPO : itemPOS) {
                try {
                    String imageUrl = itemPO.getImgUrl();
                    String fileName = ConstantUtil.LOCAL_IMAGE_STORAGE + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                    DownloadUtil.download(imageUrl, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                itemMapper.updateItem(itemPO);
            }
            showInfo("数据库更新完成");
        });
    }

    private void initCrawlingController() {
        this.crawlingController = CrawlingController.getInstance();
        crawlingController.setMainController(this);
    }

    private MainController() {
        initCrawlingController();
        initUpdateService();
    }
}
