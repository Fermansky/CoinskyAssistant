package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.felixhua.coinskyassistant.mapper.ItemMapper;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import com.felixhua.coinskyassistant.util.LogUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;

public class MainController {
    private static final MainController mainController = new MainController();
    public MessagePane messagePane;
    public SettingStage settingStage;
    public ObjectProperty<VoiceAssistant> voiceAssistantProperty = new SimpleObjectProperty<>();
    private ItemMapper itemMapper;
    private CrawlingController crawlingController;
    private ArrayList<VoiceAssistant> voiceAssistants = new ArrayList<>();

    public static MainController getInstance() {
        return mainController;
    }

    public void updateAndInsertItem(ItemPO itemPO) {
        if (itemPO.getPrice() == 0) {
            return ;
        }
        try {
            int rowsUpdated = itemMapper.updateItem(itemPO); // 尝试执行更新操作
            if (rowsUpdated == 0) { // 更新失败，执行插入操作
                itemMapper.insertItem(itemPO);
                itemMapper.insertImages(itemPO.getImagePOS());
                LogUtil.log("插入数据 " + itemPO.getName());
            } else {
                LogUtil.log("更新数据 " + itemPO.getName());
            }
        } catch (Exception e) {
            // 处理异常
            LogUtil.log(LogLevel.WARNING, e.getMessage());
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
    public void setVoiceAssistant(String name) {
        for(VoiceAssistant voiceAssistant : voiceAssistants) {
            if(voiceAssistant.getName().equals(name)) {
                voiceAssistantProperty.setValue(voiceAssistant);
                return ;
            }
        }
        LogUtil.log("未能找到名为" + name + "的语音资源包，将加载默认语音。");
    }

    public CrawlingController getCrawlingController() {
        return crawlingController;
    }

    private void initCrawlingController() {
        this.crawlingController = CrawlingController.getInstance();
        crawlingController.setMainController(this);
    }

    public void initVoiceAssistant() {
        VoiceAssistant paimon = new VoiceAssistant("paimon", "派蒙");
        paimon.setAvatar("paimon");
        paimon.setDescription("基于VITS-PAIMON项目");

        VoiceAssistant nilou = new VoiceAssistant("nilou", "妮露");
        nilou.setAvatar("nilou");
        nilou.setDescription("https://genshinvoice.top/v2/");

        VoiceAssistant yanfei = new VoiceAssistant("yanfei", "烟绯");
        yanfei.setAvatar("yanfei");
        yanfei.setDescription("https://genshinvoice.top/v2/");

        voiceAssistants.add(paimon);
        voiceAssistants.add(nilou);
        voiceAssistants.add(yanfei);

        settingStage.addVoiceAssistant(paimon);
        settingStage.addVoiceAssistant(nilou);
        settingStage.addVoiceAssistant(yanfei);
    }

    private MainController() {
        initCrawlingController();
    }
}
