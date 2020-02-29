package com.oxygen.util;

import com.joy.error.BusinessException;
import com.joy.httpclient.HTTPResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @program: spider-api
 * @description: 重试类
 * @author: Mr.Yang
 * @create: 2019-11-14 14:46
 **/


public abstract class RetryTemplate {
    protected static final Logger log = LoggerFactory.getLogger(RetryTemplate.class);

    private static final int DEFAULT_RETRY_TIME = 1;

    private int retryTime = DEFAULT_RETRY_TIME;

    // 重试的睡眠时间
    private int sleepTime = 0;

    public int getSleepTime() {
        return sleepTime;
    }

    public RetryTemplate setSleepTime(int sleepTime) {
        if (sleepTime < 0) {
            throw new IllegalArgumentException("sleepTime should equal or bigger than 0");
        }

        this.sleepTime = sleepTime;
        return this;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public RetryTemplate setRetryTime(int retryTime) {
        if (retryTime <= 0) {
            throw new IllegalArgumentException("retryTime should bigger than 0");
        }

        this.retryTime = retryTime;
        return this;
    }

    /**
     * 重试的业务执行代码
     * 失败时请抛出一个异常
     * <p>
     * todo 确定返回的封装类，根据返回结果的状态来判定是否需要重试
     *
     * @return
     */
    protected abstract Object doBiz() throws Exception;

    /**
     * 重试的自定义异常抛出类，传入业务执行的结果，可按结果返回异常
     * 失败时请随便抛出一个异常
     * <p>
     * todo 确定返回的封装类，根据返回结果的状态来判定是否需要重试
     *
     * @return
     */
    protected void throwException(Object params) throws Exception {
        HTTPResponse response = (HTTPResponse) params;
        int status = response.statusCode;
        String html = response.body;
        if ((StringUtils.isBlank(html) && judgeStatus(status) > 3) || html.contains("服务器繁忙，请稍后再试") || html.contains("404 - Not Found")
                || html.contains("504 Gateway")) {
            throw new BusinessException(InnoErrorCode.OFFICIAL_ERROR_MESSAGE, "官网已经被查询的人团团围住服务不过来了，请您稍后再试！");
        }
        if (judgeStatus(status) > 3) {
            throw new BusinessException(InnoErrorCode.OFFICIAL_ERROR_MESSAGE, "官网返回异常，请您稍候重试！");
        }
    }

    public int judgeStatus(int status) {
        if (status >= 500) {
            // 服务器异常
            return 5;
        } else if (status >= 400) {
            // 拒绝访问
            return 4;
        } else if (status >= 300) {
            // 重定向
            return 3;
        } else if (status >= 200) {
            // 正常访问返回
            return 2;
        } else if (status >= 100) {
            // 继续访问
            return 1;
        } else
            // 异常请求
            return 0;
    }

    public Object execute() throws InterruptedException {
        for (int i = 0; i < retryTime; i++) {
            try {
                Object result = doBiz();
                if (i != retryTime - 1) {
                    throwException(result);
                }
                return result;
            } catch (Exception e) {
                log.info("抓取重试第{}次",i);
//                log.error("业务执行出现异常，e: {}", e);
                if (i == retryTime - 1) {
                    return e;
                }
                Thread.sleep(sleepTime);
            }
        }

        return null;
    }


    public Object submit(ExecutorService executorService) {
        if (executorService == null) {
            throw new IllegalArgumentException("please choose executorService!");
        }

        return executorService.submit((Callable) () -> execute());
    }

    public static void returnErrorju(Object ans) throws BusinessException {
        if (ans instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ans;
            throw businessException;
        } else if (ans instanceof Exception) {
            Exception exception = (Exception) ans;
            log.error("抓取时出现异常" + ExcpUtil.getStackTraceString(exception));
            throw new BusinessException(InnoErrorCode.OUTER_SERVICE_ERROR, "官网连接失败,请重试");
        }

    }
}
