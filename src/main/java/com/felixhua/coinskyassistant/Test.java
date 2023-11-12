package com.felixhua.coinskyassistant;

import com.felixhua.coinskyassistant.entity.ItemPO;
import com.felixhua.coinskyassistant.mapper.ItemMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        InputStream inputStream;
        SqlSessionFactory sqlSessionFactory = null;
        SqlSession sqlSession;
        try {
//            inputStream = Test.class.getClassLoader().getResourceAsStream(resource);
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            inputStream.reset();
//            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
//            String result = scanner.hasNext() ? scanner.next() : "";
//            System.out.println(result);
            inputStream.close();
            sqlSessionFactory.getConfiguration().addMapper(ItemMapper.class);
            sqlSession = sqlSessionFactory.openSession(true);
            ItemMapper mapper = sqlSession.getMapper(ItemMapper.class);
            List<ItemPO> itemPOS = mapper.selectAllItems();
            System.out.println(itemPOS.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
