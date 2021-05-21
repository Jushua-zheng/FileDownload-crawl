import com.zc.filedownload.config.CrawlMeta;
import com.zc.filedownload.net.crawlJob.SimpleCrawlJob;
import com.zc.filedownload.result.CrawlResult;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zheng
 * @since 2021/5/21
 */
public class CrawlJobTest {

    /**
     * 测试我们写的最简单的一个爬虫,
     *
     * 目标是爬取一篇博客
     */
    @Test
    public void testFetch() throws InterruptedException {
        String url = "https://mp.weixin.qq.com/mp/audio?_wxindex_=0&scene=104&__biz=MzIzNjM0MjM3NQ==&mid=2247489255&idx=1&voice_id=MzIzNjM0MjM3NV8yMjQ3NDg5MjU0&sn=095dbf063a352ad2ce139c0c31a95490&uin=&key=&devicetype=Windows+10+x64&version=6302019a&lang=zh_CN&ascene=1&fontgear=2";
        Set<String> selectorRules = new HashSet<>();
        selectorRules.add(".mp3");

        CrawlMeta crawlMeta = new CrawlMeta(url,selectorRules);

        SimpleCrawlJob job = new SimpleCrawlJob(1);
        job.setCrawlMeta(crawlMeta);
        Thread thread = new Thread(job, "crawlerDepth-test");
        thread.start();


        thread.join();
        List<CrawlResult> result = job.getCrawlResults();
        System.out.println(result);
    }
}
