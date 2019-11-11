package com.vc.onlinepay.utils.http;

import org.apache.http.Header;

public class HttpClientResult {

    private static final long serialVersionUID = 2168152194164783950L;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 请求头
     */
    private Header[] headers;

    /**
     * 响应数据
     */
    private String content;

    public HttpClientResult() {
    }

    public HttpClientResult(Integer code) {
        this.code = code;
    }

    public HttpClientResult(String content) {
        this.content = content;
    }

    public HttpClientResult(Integer code, String content) {
        this.code = code;
        this.content = content;
    }
    /**全参数构造方法**/
    public HttpClientResult(Integer code, Header[] headers, String content){
        this.code = code;
        this.headers = headers;
        this.content = content;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return new StringBuilder("{")
                .append("\"code\":")
                .append(code)
                .append(",\"content\":\"")
                .append(null != content?(content.startsWith("{")?content.replaceAll("\"","\\\\\""):content):"").append('\"')
                .append('}').toString();
    }
}
