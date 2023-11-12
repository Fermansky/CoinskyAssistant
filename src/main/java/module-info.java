module CoinskyAssistant {
    requires lombok;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.mybatis;
    requires javafx.media;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;

    opens com.felixhua.coinskyassistant.entity to com.google.gson;
    exports com.felixhua.coinskyassistant;
}