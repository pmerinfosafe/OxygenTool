package com.oxygen.http;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * @program: qxygenTool-http
 * @description: interface
 * @author: pmer_infoSafe
 * @create: 2019-11-14 14:46
 **/
public interface HTTPClient {

    /**
     * Fetch HTTP response by given HTTP request,
     *
     * @param request  Specify which to be fetched.
     *
     * @return the response to the request.
     * @throws IOException  connection problem.
     * @throws URISyntaxException  url syntax problem.
     */
    HTTPResponse fetch(HTTPRequest request)
        throws IOException, URISyntaxException;

    /**
     * Close http client and release resource
     *
     */
    void close();
}
