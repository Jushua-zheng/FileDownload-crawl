package com.zc.filedownload.config;


import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zheng
 * @since 2021/5/21
 * 爬虫配置类
 */
public class CrawlMeta {
    /**
     * 待爬去的网址
     */
    private String url;

    /**
     * 获取指定内容的规则, 因为一个网页中，你可能获取多个不同的内容， 所以放在集合中
     */
    private Set<String> selectorRules;

    public CrawlMeta(String url, Set<String> selectorRules) {
        this.url = url;
        this.selectorRules = selectorRules;
    }

    public CrawlMeta() {}


    // 这么做的目的就是为了防止NPE, 也就是说支持不指定选择规则
    public Set<String> getSelectorRules() {
        return selectorRules != null ? selectorRules : new HashSet<>();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSelectorRules(Set<String> selectorRules) {
        this.selectorRules = selectorRules;
    }

    public String getUrl() {
        return url;
    }
}
