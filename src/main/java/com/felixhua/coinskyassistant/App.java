package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.constants.Constant;
import com.felixhua.coinskyassistant.controller.MainController;
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
import java.util.Objects;

public class App extends Application {
    private long launchTime;
    private MainController controller;
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    @Override
    public void init(){
        LogUtil.log("钱币天堂智能助手启动成功，当前版本：" + Constant.VERSION);
        launchTime = System.currentTimeMillis();
    }

    @Override
    public void start(Stage primaryStage) {
        controller = MainController.getInstance();
        ContentScene scene = new ContentScene(new MessagePane(controller), primaryStage);
        SettingStage settingStage = new SettingStage(controller);

        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sqlSession = sqlSessionFactory.openSession(true);
        ItemMapper mapper = sqlSession.getMapper(ItemMapper.class);
        controller.setItemMapper(mapper);

        VoiceAssistant paimon = new VoiceAssistant("paimon");
        paimon.setAvatar("paimon");
        settingStage.addVoiceAssistant(paimon);

        scene.setFill(new Color(0, 0, 0, 0.5));

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("钱币天堂上架提醒bot");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResource("/icon.png")).toExternalForm()));
        primaryStage.show();

        VoiceUtil.play(VoicePrompt.DAILY_GREETING);
    }

    @Override
    public void stop() {
        sqlSession.close();
        long stopTime = System.currentTimeMillis();
        long runningTime = stopTime - launchTime;
        System.out.println("程序运行结束，耗时" + runningTime + "毫秒。");
        System.out.println("平均每次爬取耗时" + controller.getCrawlingController().getAverageCrawlingTime() + "毫秒。");
        System.out.println("共计爬取失败" + controller.getCrawlingController().getFailureCount() + "次。");
        System.out.println("共计爬取成功" + controller.getCrawlingController().getSuccessCount() + "次。");
        LogUtil.log("程序运行结束，耗时" + runningTime + "毫秒。");
        LogUtil.log("平均每次爬取耗时" + controller.getCrawlingController().getAverageCrawlingTime() + "毫秒。");
        LogUtil.log("共计爬取失败" + controller.getCrawlingController().getFailureCount() + "次。");
        LogUtil.log("共计爬取成功" + controller.getCrawlingController().getSuccessCount() + "次。");
        System.exit(0);
    }

}
