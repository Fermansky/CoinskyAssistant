package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.Crawler;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.mapper.ItemMapper;
import com.felixhua.coinskyassistant.service.UpdateService;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import com.felixhua.coinskyassistant.util.ConstantUtil;
import com.felixhua.coinskyassistant.util.DownloadUtil;
import com.felixhua.coinskyassistant.util.LogUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainController {
    private static final MainController mainController = new MainController();
    public MessagePane messagePane;
    public Crawler crawler;
    public SettingStage settingStage;
    public ObjectProperty<VoiceAssistant> voiceAssistantProperty = new SimpleObjectProperty<>();
    private SqlSessionFactory sqlSessionFactory;
    private ItemMapper itemMapper;
    private UpdateService updateService;

    public static MainController getInstance() {
        return mainController;
    }

    public void log(String info) {
//        LogUtil.appendLog(new Date() + " " + info + "\n");
//        settingStage.appendLog();
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

    public ReadOnlyStringProperty getUpdateServiceMessageProperty(){
        return updateService.messageProperty();
    }

    public void showInfo(String info) {
        settingStage.getBottomInfo().setText(info);
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
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

    public Crawler getCrawler() {
        return crawler;
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

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public ItemMapper getItemMapper() {
        return itemMapper;
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

    private MainController() {
        initUpdateService();
    }
}
