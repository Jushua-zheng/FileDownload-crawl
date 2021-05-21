package com.zc.filedownload.net;

/**
 * @author zheng
 * @since 2021/5/21
 */
public abstract class AbstractJob implements Ijob {
    public void beforeRun(){}

    public void afterRun(){}

    public void run(){
        beforeRun();
        try {
            doFetchPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        afterRun();
    }

    /**
     * 具体的抓去网页的方法， 需要子类来补全实现逻辑
     *
     * @throws Exception
     */
    public abstract void doFetchPage() throws Exception;
}
