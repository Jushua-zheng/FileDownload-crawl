package com.zc.filedownload.result;

import lombok.*;
import org.jsoup.nodes.Document;
import java.util.*;

/**
 * @author zheng
 * @since 2021/5/21
 */
@Getter
@Setter
@ToString
public class CrawlResult {

    public static final Status SUCCESS = new Status(200,"success");
    /**
     * 爬取的网址
     */
    private String url;


    /**
     * 爬取的网址对应的 DOC 结构
     */
    private Document htmlDoc;

    private Status status;

    /**
     * 选择的结果，key为选择规则，value为根据规则匹配的结果
     */
    private Map<String, List<String>> result;

    /**
     * 当前页面的下级页面集合
     */
    private Set<String> nextUrl;

    public Set<String> getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(Set<String> nextUrl) {
        this.nextUrl = nextUrl;
    }

    public Map<String, List<String>> getResult() {
        if(result == null)
            result = new HashMap<String, List<String>>();
        return result;
    }

    public void setStatus(int statusCode, String reasonPhrase) {
        this.status = new Status(statusCode, reasonPhrase);
    }

    public Document getHtmlDoc() {
        return htmlDoc;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    static class Status {
        private int code;

        private String msg;
    }
}
