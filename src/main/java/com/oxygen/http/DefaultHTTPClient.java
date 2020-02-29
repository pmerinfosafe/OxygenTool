package com.oxygen.http;

import com.oxygen.util.BusinessException;
import com.oxygen.util.InnoErrorCode;
import com.oxygen.util.RetryTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


public class DefaultHTTPClient implements HTTPClient {

    private HttpHost proxy;
    // Create http client
    private CloseableHttpClient client;
    private CookieStore cookieStore;
    private boolean isPool = false;
    private HttpClientContext context;

    private String proxy_user = "";

    public String getProxy_user() {
        return proxy_user;
    }

    public void setProxy_user(String proxy_user) {
        this.proxy_user = proxy_user;
    }

    public String getProxy_name() {
        return proxy_name;
    }

    public void setProxy_name(String proxy_name) {
        this.proxy_name = proxy_name;
    }

    public boolean isIs_proxyCheck() {
        return is_proxyCheck;
    }

    public void setIs_proxyCheck(boolean is_proxyCheck) {
        this.is_proxyCheck = is_proxyCheck;
    }

    private String proxy_name = "";
    private boolean is_proxyCheck = false;

    /**
     * @Description: 创建有代理的请求工具
     * @Param: host 代理
     * @return: 无
     * @Author: Mr.Yang
     * @Date: 2019/3/8
     */
    public DefaultHTTPClient(String host) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host);
        client = createSSLClientDefault();
    }

    public DefaultHTTPClient() {
        cookieStore = new BasicCookieStore();
        client = createSSLClientDefault();
    }

    public DefaultHTTPClient(String host, int port) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host, port);
        client = createSSLClientDefault();
    }

    public DefaultHTTPClient(String host, int port, String scheme) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host, port, scheme);
        client = createSSLClientDefault();
    }

    public DefaultHTTPClient(HTTPClientPooling connMgr) {
        cookieStore = new BasicCookieStore();
        createpoolClientDefault(connMgr);
    }

    public void RefreshHTTPClient() {
        cookieStore = new BasicCookieStore();
        if (isPool)
            client = HttpClients.custom().build();
        else
            client = createSSLClientDefault();
    }

    public DefaultHTTPClient(String host, HTTPClientPooling connMgr) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host);
        createpoolClientDefault(connMgr);
    }

    public DefaultHTTPClient(String host, int port, HTTPClientPooling connMgr) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host, port);
        createpoolClientDefault(connMgr);
    }

    //https信任所有请求创建
    public void createpoolClientDefault(HTTPClientPooling connMgr) {
        try {
            cookieStore = new BasicCookieStore();
            isPool = true;
            client = HttpClients.custom().setConnectionManager(connMgr.getConnMgr()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = createSSLClientDefault();
    }

    public DefaultHTTPClient(String host, int port, String scheme, HTTPClientPooling connMgr) {
        cookieStore = new BasicCookieStore();
        this.proxy = new HttpHost(host, port, scheme);
        client = createSSLClientDefault();
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    public void setProxy(String host, int port) {
        this.proxy = new HttpHost(host, port);
    }

    public void setProxy(String host, int port, String scheme) {
        this.proxy = new HttpHost(host, port, scheme);
    }

    //https信任所有请求创建
    public CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            /**
             * HTTP connection相关配置（默认配置 和 某个host的配置）
             * 一般不修改HTTP connection相关配置，故不设置
             */
            //消息约束
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
                    .setMaxLineLength(2000)
                    .build();
            //Http connection相关配置
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();
            SSLContext sslcontext = SSLContexts.createDefault();
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1.2"},
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", trustAllHttpsCertificates())  // 用来配置支持的协议
                    .build();

            //一般不修改HTTP connection相关配置，故不设置
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);

            /**
             * request请求相关配置
             */
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(2 * 1000)         //连接超时时间
                    .setSocketTimeout(2 * 1000)          //读超时时间（等待数据超时时间）
                    .setConnectionRequestTimeout(500)    //从池中获取连接超时时间
                    .setStaleConnectionCheckEnabled(true)//检查是否为陈旧的连接，默认为true，类似testOnBorrow
                    .build();

            /**
             * 重试处理
             * 默认是重试3次
             */
            //禁用重试(参数：retryCount、requestSentRetryEnabled)
            HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);
            //自定义重试策略
            HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

                public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                    //Do not retry if over max retry count
                    if (executionCount >= 3) {
                        return false;
                    }
                    //Timeout
                    if (exception instanceof InterruptedIOException) {
                        return false;
                    }
                    //Unknown host
                    if (exception instanceof UnknownHostException) {
                        return false;
                    }
                    //Connection refused
                    if (exception instanceof ConnectTimeoutException) {
                        return false;
                    }
                    //SSL handshake exception
                    if (exception instanceof SSLException) {
                        return false;
                    }

                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                    //Retry if the request is considered idempotent
                    //如果请求类型不是HttpEntityEnclosingRequest，被认为是幂等的，那么就重试
                    //HttpEntityEnclosingRequest指的是有请求体的request，比HttpRequest多一个Entity属性
                    //而常用的GET请求是没有请求体的，POST、PUT都是有请求体的
                    //Rest一般用GET请求获取数据，故幂等，POST用于新增数据，故不幂等
                    if (idempotent) {
                        return true;
                    }

                    return false;
                }
            };
            // 创建HttpClient上下文
            context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            return HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(defaultRequestConfig) //默认请求配置
                    .setRetryHandler(myRetryHandler).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
            throw new BusinessException(InnoErrorCode.NET_IS_FAIL, "网络走丢了，请重新操作！");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BusinessException(InnoErrorCode.NET_IS_FAIL, "网络走丢了，请重新操作！");
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new BusinessException(InnoErrorCode.NET_IS_FAIL, "网络走丢了，请重新操作！");
        }
    }

    private static SSLConnectionSocketFactory trustAllHttpsCertificates() {
        SSLConnectionSocketFactory socketFactory = null;
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");//sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, null);
            socketFactory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return socketFactory;
    }

    static class miTM implements TrustManager, X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
            //don't check
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
            //don't check
        }
    }

    public Map<String, String> fetchhtml(final HTTPRequest request) {
        try {
            Object ans = new RetryTemplate() {
                @Override
                protected Object doBiz() throws Exception {
                    HTTPResponse response = fetch(request);
                    return response;
                }
            }.setRetryTime(request.getretryNum()).setSleepTime(request.getWaitTime()).execute();
            RetryTemplate.returnErrorju(ans);
            HTTPResponse response = (HTTPResponse) ans;
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("status", response.statusCode + "");
            resultMap.put("html", response.body);
            if (StringUtils.isNotBlank(response.locationUrl))
                resultMap.put("location", response.locationUrl);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BusinessException(InnoErrorCode.NET_IS_FAIL, "网络走丢了，请重新操作！");
    }

    public HTTPResponse fetch(HTTPRequest request) throws IOException, URISyntaxException {
        // Build URI
        URIBuilder uriBuilder = new URIBuilder(request.url);
//        System.out.println(uriBuilder.getHost());
//        for (Map.Entry<String, String> entry : request.parameters.entrySet()) {
//            uriBuilder.addParameter(entry.getKey(), entry.getValue());
//        }

        // Build request
        RequestConfig reqConfig;
        if (null == proxy || StringUtils.isBlank(proxy.getHostName()))
            reqConfig = RequestConfig.custom()
                    //.setSocketTimeout(5 * 1000)
                    .setSocketTimeout(5 * 60 * 1000)
                    .setConnectTimeout(request.connectTimeout)
                    .setConnectionRequestTimeout(request.requestTimeout)
                    .setRedirectsEnabled(request.needRedirect)
                    .build();
        else
            reqConfig = RequestConfig.custom()
                    .setSocketTimeout(5 * 60 * 1000)
                    .setConnectTimeout(request.connectTimeout)
                    .setConnectionRequestTimeout(request.requestTimeout)
                    .setProxy(this.proxy)
                    .setRedirectsEnabled(request.needRedirect)
                    .build();

        RequestBuilder reqBuilder = RequestBuilder.create(request.method.name())
                .setUri(uriBuilder.build())
                .setEntity(new StringEntity(request.body, "UTF-8"))
                .setConfig(reqConfig);
        if (null != request.entity) {
            reqBuilder.setEntity(request.entity);
        } else if (null != request.getParameters() && !request.getParameters().isEmpty()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : request.parameters.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            reqBuilder.setEntity(new UrlEncodedFormEntity(params, request.enCode));
        }
        if (is_proxyCheck) {
//            String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
//            byte[] encodedAuth = Base64.encodeBase64(
//                    auth.getBytes(Charset.forName("ISO-8859-1")));
//            String authHeader = "Basic " + new String(encodedAuth);
//            reqBuilder.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            AuthScope authscope = new AuthScope(proxy.getHostName(), proxy.getPort());
//            final  String proxyUser = DEFAULT_USER;
//            final  String proxyPass = DEFAULT_PASS;
            Credentials credentials = new UsernamePasswordCredentials(proxy_name, proxy_user);
            // 设置认证
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authscope, credentials);
            context.setCredentialsProvider(provider);
//            DefaultHttpClient httpclient = (DefaultHttpClient) this.client;
//            httpclient.getCredentialsProvider().setCredentials(authscope, credentials);
            // httpReq.setConfig(config);
//            this.client = httpclient;
//            client = HttpClients.custom().setDefaultCredentialsProvider(provider).build();

        }
        if (!request.getHeadercover()) {
            reqBuilder.setHeader("Connection", "keep-alive");
            reqBuilder.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            reqBuilder.setHeader("User-Agent", "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            reqBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            reqBuilder.setHeader("Accept-Encoding", "gzip, deflate, br");
            reqBuilder.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        }
        for (Map.Entry<String, String> entry : request.headers.entrySet()) {
            reqBuilder.setHeader(entry.getKey(), entry.getValue());
        }

        if (StringUtils.isNotBlank(request.body)) {
            reqBuilder.setHeader("Content-Type", "application/json; charset=UTF-8");
        }
        // Fetch http response
        try {
            CloseableHttpResponse response = client.execute(reqBuilder.build(), context);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);

            //处理http返回码302的情况
            String locationUrl = "";
            HTTPResponse fetch = null;
            if (response.getStatusLine().getStatusCode() == 302) {
                if (response.getHeaders("Location").length > 0) {
                    locationUrl = response.getLastHeader("Location").getValue();
                    if (StringUtils.isNotBlank(locationUrl)) {
                        try {
                            if (locationUrl.substring(0, 1).equals("/"))
                                locationUrl = "http://" + reqBuilder.getUri().getHost() + locationUrl;
                            else if (!locationUrl.contains(reqBuilder.getUri().getHost()) && !locationUrl.contains("http://") && !locationUrl.contains("https://")) {
                                locationUrl = "http://" + reqBuilder.getUri().getHost() + "/" + locationUrl;
                            }
                            if (request.needRedirect == true) {
                                HTTPRequest httpRequest = new HTTPRequest(HTTPMethod.GET, locationUrl);
                                fetch = fetch(httpRequest);
                            }
                        } catch (Exception e) {
//                        logger.info("post异常" + e);

                        }
                    }
                }
            }
            try {
                // May throw IOException
                HTTPResponse res = new HTTPResponse()
                        .setRequest(request)
                        .setStatusCode(response.getStatusLine().getStatusCode())
                        .setReason(response.getStatusLine().getReasonPhrase())
                        .setBody(EntityUtils.toString(bufferedEntity, request.getEnCode()))
                        .setEntity(bufferedEntity)
                        .setLocationUrl(locationUrl);
                if (null != fetch) {
                    res.setBody(fetch.body);
                }
                for (Header header : response.getAllHeaders()) {
                    res.addHeader(header.getName(), header.getValue());
                }
                //需要加入返回状态判断是否需要抛出异常
                return res;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(InnoErrorCode.NET_IS_FAIL, "网络走丢了，请重新操作！");
        } finally {
            if (!isPool)
                client.close();
        }
    }

    public void close() {
        // Do nothing
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                // in case of a problem or the connection was aborted
            }
            client = null;
        }
    }

    public CookieStore getcookie() {
//        context
        cookieStore = context.getCookieStore();
        return cookieStore;
    }

    public List<Cookie> getCookieList() {
//        context
        CookieStore cookieStore = getcookie();
        List<Cookie> cookies = cookieStore.getCookies();
        return cookies;
    }

    public Map<String, String> getcookieMap() {
        cookieStore = context.getCookieStore();
        Map<String, String> cookieMap = new HashMap<String, String>();
        List<Cookie> cookies = cookieStore.getCookies();
        Iterator<Cookie> iterator = cookies.iterator();
        while (iterator.hasNext()) {
            Cookie next = iterator.next();
            cookieMap.put(next.getName(), next.getValue());
        }
        return cookieMap;
    }

    /**
     * 获取当前的httpclient的cookie
     *
     * @return String格式的cookies
     * @throws BusinessException
     */
    public String getCookieString() {
        String cookieString = "";
        cookieStore = context.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            cookieString = cookieString + cookie.getName() + "=" + cookie.getValue() + ";";
        }
        return cookieString;

    }

    public void cleancookie() {
        this.cookieStore.clear();
        context.setCookieStore(cookieStore);
    }

    public void setcookie(CookieStore cookieStore) {

        this.cookieStore = cookieStore;
        context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        context.setCookieStore(cookieStore);
    }

    public void setcookie(Map<String, String> cookie, String status, String domain) {
        if (StringUtils.isNotBlank(status) && status.equals("0")) {
            cookieStore.clear();
        }
        for (String key :
                cookie.keySet()) {
            BasicClientCookie basicClientCookie = new BasicClientCookie(key, cookie.get(key));
            basicClientCookie.setDomain(domain);
            basicClientCookie.setPath("/");
            cookieStore.addCookie(basicClientCookie);
        }
        context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        context.setCookieStore(cookieStore);

    }
}
