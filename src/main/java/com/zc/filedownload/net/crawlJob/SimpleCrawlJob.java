package com.zc.filedownload.net.crawlJob;

import com.zc.filedownload.config.CrawlHttpConf;
import com.zc.filedownload.config.CrawlMeta;
import com.zc.filedownload.net.AbstractJob;
import com.zc.filedownload.net.util.HttpUtils;
import com.zc.filedownload.result.CrawlResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zheng
 * @since 2021/5/21
 */
public class SimpleCrawlJob extends AbstractJob {
    /**
     * 配置项信息
     */
    private CrawlMeta crawlMeta;


    /**
     * 存储爬取的结果
     */
    private CrawlResult crawlResult;

    /**
     * 批量查询的结果
     */
    private List<CrawlResult> crawlResults = new ArrayList<>();


    /**
     * 爬网页的深度, 默认为0， 即只爬取当前网页
     */
    private int depth = 0;

    /**
     *  检验URL的正则表达式
     */
    private static final String URL_PATTERN = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    public SimpleCrawlJob(int depth) {
        super();

        this.depth = depth;
    }

    /**
     * 执行抓取网页
     */
    public void doFetchPage() throws Exception {
        doFetchNextPage(0, this.crawlMeta.getUrl());
        this.crawlResult = this.crawlResults.get(0);
    }


    private void doFetchNextPage(int currentDepth, String url) throws Exception {
        CrawlHttpConf httpConf = new CrawlHttpConf();
        HttpResponse response = HttpUtils.request(new CrawlMeta(url, this.crawlMeta.getSelectorRules()), httpConf);
        String res = EntityUtils.toString(response.getEntity());
        CrawlResult result;
        if (response.getStatusLine().getStatusCode() != 200) { // 请求成功
            result = new CrawlResult();
            result.setStatus(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            result.setUrl(crawlMeta.getUrl());
            this.crawlResults.add(result);
            return;
        }

        result = doParse(res);
        crawlResults.add(result);

        // 超过最大深度， 不继续爬
        if (currentDepth >= depth) {
            return;
        }

        result.getNextUrl();


        Elements elements = result.getHtmlDoc().select("a[href]");
        for(Element element: elements) {
            String nextUrl = element.attr("href");
            if(Pattern.matches(URL_PATTERN,nextUrl)){
                doFetchNextPage(currentDepth + 1, nextUrl);
            }else{
                nextUrl = url + nextUrl;
                if(Pattern.matches(URL_PATTERN,nextUrl)){
                    doFetchNextPage(currentDepth + 1, nextUrl);
                }else{
                    continue;
                }
            }
        }
    }



    private CrawlResult doParse(String html) {
        CrawlResult crs = new CrawlResult();
        String url = crawlMeta.getUrl();
        Document doc = Jsoup.parse(html);
        crs.setUrl(url);
        crs.setHtmlDoc(doc);
        //页面所有单引号包裹的属性内容
        List<String> urls = new ArrayList<>();
        //不符合URL的正则表达书校验的内容
        List<String> err_urls = new ArrayList<>();

        //解析页面中所有单引号包裹的内容
        while(true){
            int index = html.indexOf("'");
            if(index == -1){
                break;
            }else{
                html = html.substring(index+1);
                index = html.indexOf("'");
                urls.add(html.substring(0,index));
                html = html.substring(index+1);
            }
        }

        //获取当前页面的路径，用于拼接相对路径
        int index = url.indexOf("?") == -1 ? url.length() : url.indexOf("?");
        url = url.substring(0,index);
        if(url.charAt(url.length()-1) != '/') {
            String[] strs = url.split("/");
            url = url.substring(0, url.indexOf(strs[strs.length - 1]));
        }

        //校验URL格式
        for (int i = 0;i < urls.size(); i++) {
            String urlStr = urls.get(i);
            if(!Pattern.matches(URL_PATTERN,urlStr)){
                String newUrl = url + urlStr;
                if(Pattern.matches(URL_PATTERN,newUrl)){
                    urls.set(i,newUrl);
                }else{
                    err_urls.add(urlStr);
                }
            }
        }

        //移除格式错误的URL
        for (String s: err_urls) {
            urls.remove(s);
        }

        //检查包含所需文件后缀的URL放入结果集
        Set<String> rules = crawlMeta.getSelectorRules();
        Map result = crs.getResult();
        for (String rule : rules) {
            List<String> fileUrl = new ArrayList<>();
            for (int i = 0;i < urls.size(); i++) {
                String s = urls.get(i);
                if(s.contains(rule)){
                    fileUrl.add(s);
                    urls.remove(i);
                }
            }
            result.put(rule,fileUrl);
        }
        Set<String> set = new HashSet<>(urls);
        crs.setNextUrl(set);
        crs.setStatus(CrawlResult.SUCCESS);
        return crs;
    }

    public void setCrawlMeta(CrawlMeta crawlMeta) {
        this.crawlMeta = crawlMeta;
    }

    public CrawlResult getCrawlResult() {
        return crawlResult;
    }

    public List<CrawlResult> getCrawlResults() {
        return crawlResults;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
