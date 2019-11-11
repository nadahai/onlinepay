package com.vc.onlinepay.delay;

/**
 * @author yang
 */
public class RetMessage {
    /**
     * 已重试次数
     */
    private int times;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 接口地址
     */
    private String url;

    public RetMessage() {
        super();
    }

    public RetMessage(String url) {
        super();
        this.times = 1;
        this.success = false;
        this.url = url;
    }

    public RetMessage(int times, boolean success) {
        super();
        this.times = times;
        this.success = success;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
