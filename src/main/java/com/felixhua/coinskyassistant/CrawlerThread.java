package com.felixhua.coinskyassistant;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.concurrent.Callable;

/**
 * Experimental. Used in the crawlWithMultipleThread Method.
 */
public class CrawlerThread implements Callable<String> {
    private final Crawler crawler;

    public CrawlerThread(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public String call() throws Exception {
        String html = null;
        try (WebClient webClient = new WebClient()) {
            long startTime = System.currentTimeMillis();
            WebClientOptions options = webClient.getOptions();
            options.setJavaScriptEnabled(true);
            options.setCssEnabled(false);
            options.setRedirectEnabled(true);

            HtmlPage page = webClient.getPage(Crawler.url);
            int jsLeft = webClient.waitForBackgroundJavaScript(100000);
            if (jsLeft == 0) {
                System.out.println(Thread.currentThread().getName() + "获取成功，耗时" +
                        (System.currentTimeMillis() - startTime) + "ms.");
                html = page.asXml();
            } else {
                System.out.println(Thread.currentThread().getName() + "获取失败，剩余" + jsLeft);
                return "n";
            }
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName());
        }
        return html;
    }
}
