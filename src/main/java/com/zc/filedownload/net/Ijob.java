package com.zc.filedownload.net;

/**
 * @author zheng
 * @since 2021/5/21
 */
public interface Ijob extends Runnable{
    /**
     * 在job执行之前回调的方法
     */
    void beforeRun();


    /**
     * 在job执行完毕之后回调的方法
     */
    void afterRun();
}
