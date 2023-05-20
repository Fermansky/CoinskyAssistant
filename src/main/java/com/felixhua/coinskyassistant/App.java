package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.controller.MainController;
import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.entity.VoiceAssistant;
import com.felixhua.coinskyassistant.enums.VoicePrompt;
import com.felixhua.coinskyassistant.mapper.ItemMapper;
import com.felixhua.coinskyassistant.ui.ContentScene;
import com.felixhua.coinskyassistant.ui.MessagePane;
import com.felixhua.coinskyassistant.ui.SettingStage;
import com.felixhua.coinskyassistant.util.LogUtil;
import com.felixhua.coinskyassistant.util.VoiceUtil;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class App extends Application {
    private long launchTime;
    private long stopTime;
    private Crawler crawler;
    private MainController controller;
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    @Override
    public void init(){
        launchTime = System.currentTimeMillis();
    }

    @Override
    public void start(Stage primaryStage) {
        controller = MainController.getInstance();
        ContentScene scene = new ContentScene(new MessagePane(controller), primaryStage);
        crawler = new Crawler(controller);
        SettingStage settingStage = new SettingStage(controller);

        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            controller.setSqlSessionFactory(sqlSessionFactory);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sqlSession = sqlSessionFactory.openSession(true);
        ItemMapper mapper = sqlSession.getMapper(ItemMapper.class);
        controller.setItemMapper(mapper);
        List<ItemPO> items = mapper.selectAllItems();

        VoiceAssistant paimon = new VoiceAssistant("paimon");
        paimon.setAvatar("paimon");
        settingStage.addVoiceAssistant(paimon);

        scene.setFill(new Color(0, 0, 0, 0.5));

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("钱币天堂上架提醒bot");
        primaryStage.getIcons().add(new Image(App.class.getResource("/icon.png").toExternalForm()));
        primaryStage.show();

        VoiceUtil.play(VoicePrompt.DAILY_GREETING);

        crawler.start();
    }

    @Override
    public void stop() {
        sqlSession.close();
        stopTime = System.currentTimeMillis();
        long runningTime = stopTime - launchTime;
        System.out.println("程序运行结束，耗时" + runningTime + "毫秒。");
        System.out.println("平均每次爬取耗时" + crawler.averageCrawlTime + "毫秒。");
        System.out.println("共计爬取失败" + crawler.failureCount + "次。");
        LogUtil.log("程序运行结束，耗时" + runningTime + "毫秒。");
        LogUtil.log("平均每次爬取耗时" + crawler.averageCrawlTime + "毫秒。");
        LogUtil.log("共计爬取失败" + crawler.failureCount + "次。");
        System.exit(0);
    }

    public long getCurrentRunningTimeMillis() {
        return System.currentTimeMillis() - launchTime;
    }
}
