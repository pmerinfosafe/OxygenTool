package com.oxygen.http;

import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: qxygenTool-http
 * @description: request封装
 * @author: pmer_infoSafe
 * @create: 2019-11-14 14:46
 **/
public class HTTPRequest {

    //请求URL：http://srh.bankofchina.com/search/whpj/search.jsp
    public String url;

    //重试次数
    public int retryNum = 3;

    //重试等待时间
    public int waitTime = 1000;

    //请求头全覆盖
    public Boolean Headercover = false;

    //GET/POST方法
    public HTTPMethod method;

    public HttpEntity getEntity() {
        return entity;
    }

    public HTTPRequest setEntity(HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    //GET/POST方法
    public HttpEntity entity = null;

    //JSON格式请求参数【扩展】
    public String body = "";

    public HTTPRequest setNeedRedirect(boolean needRedirect) {
        this.needRedirect = needRedirect;
        return this;
    }

    public boolean needRedirect = true;

    public Map<String, String> headers;

    //请求查询参数
    public Map<String, String> parameters;

    public Integer connectTimeout = 60*1000;

    public Integer requestTimeout = 60*1000;

    public String getEnCode() {
        return enCode;
    }

    public HTTPRequest setEnCode(String enCode) {
        this.enCode = enCode;
        return this;
    }

    public String enCode = "utf-8";


    public HTTPRequest(final HTTPMethod method, final String url) {
        super();
        this.method = method;
        this.url = url;

        this.headers = new HashMap<String, String>();
        this.parameters = new HashMap<String, String>();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HTTPRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public HTTPRequest setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public HTTPRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public HTTPRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HTTPRequest addQueryParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public HTTPRequest addQueryParameter(String name, Integer value) {
        parameters.put(name, String.valueOf(value));
        return this;
    }

    public HTTPRequest addQueryParameter(String name, long value) {
        parameters.put(name, String.valueOf(value));
        return this;
    }

    public HTTPRequest setConnectionTimeout(Integer connectionTimeout) {
        this.connectTimeout = connectionTimeout;
        return this;
    }

    public HTTPRequest setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public int getretryNum() {
        return retryNum;
    }

    public HTTPRequest setretryNum(int retryNum) {
        this.retryNum = retryNum;
        return this;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public HTTPRequest setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public Boolean getHeadercover() {
        return Headercover;
    }

    public HTTPRequest setHeadercover(Boolean headercover) {
        Headercover = headercover;
        return this;
    }
}
