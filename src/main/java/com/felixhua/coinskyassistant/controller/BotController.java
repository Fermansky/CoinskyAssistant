package com.felixhua.coinskyassistant.controller;

import com.felixhua.coinskyassistant.UserSetting;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.enums.LogLevel;
import com.felixhua.coinskyassistant.util.LogUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.ExternalResource;

import java.net.HttpURLConnection;
import java.net.URL;

public class BotController {
    private static final BotController botController = new BotController();

    private static Bot bot;

    public static void sendMessage(ItemPO itemPO) {
        Group group = bot.getGroup(912863175);
        group.sendMessage(itemPO.getName() + "\n?" + itemPO.getPrice() + ".00\n" + itemPO.getUrl());
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(itemPO.getImgUrl()).openConnection();
            ExternalResource externalResource = ExternalResource.create(connection.getInputStream());
            ExternalResource.sendAsImage(externalResource, group);
        } catch (Exception e) {
            LogUtil.log(LogLevel.WARNING, e.getMessage());
        }
    }

    private void initBotController() {
        bot = BotFactory.INSTANCE.newBot(1943783611, BotAuthorization.byQRCode(), configuration -> {
            configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
        });
        bot.login();
    }

    private BotController() {
        if(UserSetting.mirai) {
            initBotController();
        }
    }
}
