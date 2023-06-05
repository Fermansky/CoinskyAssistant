package com.felixhua.coinskyassistant.entity;

/**
 * 该类对应于钱币天堂后端接口中data字段的参数。
 */
public class CrawlingData {
    /**
     * 商户id
     */
    int id;
    int perPage;
    /**
     * 当前页码，从0开始
     */
    int pageId;

    public CrawlingData(int id, int perPage, int pageId) {
        this.id = id;
        this.perPage = perPage;
        this.pageId = pageId;
    }

    /**
     * 默认构造方法，到涛泉堂。
     */
    public CrawlingData() {
        this.id = 2636;
        this.perPage = 1;
        this.pageId = 0;
    }

    @Override
    public String toString() {
        return String.format("{\"id\":\"%d\",\"perPage\":%d,\"pageId\":%d}", id, perPage, pageId);
    }
}
