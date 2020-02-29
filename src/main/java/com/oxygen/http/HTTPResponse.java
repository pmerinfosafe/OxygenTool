package com.oxygen.http;

import org.apache.http.entity.BufferedHttpEntity;

import java.util.HashMap;


public class HTTPResponse {

    public HTTPRequest request;
    public int statusCode;
    public String reason;
    public String body;

    public HTTPResponse setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
        return this;
    }

    public String locationUrl;
    public HashMap<String, String> headers;

    public HTTPResponse setEntity(BufferedHttpEntity entity) {
        this.entity = entity;
        return this;
    }

    public BufferedHttpEntity entity;

    public HTTPResponse() {
        this.headers = new HashMap<String, String>();
    }

    public HTTPResponse(final int statusCode) {
        this();
        this.statusCode = statusCode;
    }

    public HTTPResponse(final int statusCode, final String body) {
        super();
        this.statusCode = statusCode;
        this.body = body;
    }

    public HTTPResponse(final int statusCode, final String body, final String reason) {
        super();
        this.statusCode = statusCode;
        this.body = body;
        this.reason = reason;
    }

    public HTTPResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HTTPResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public HTTPResponse setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public HTTPResponse addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HTTPResponse setRequest(HTTPRequest request) {
        this.request = request;
        return this;
    }
}
