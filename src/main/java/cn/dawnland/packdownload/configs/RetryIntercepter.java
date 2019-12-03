package cn.dawnland.packdownload.configs;

import cn.dawnland.packdownload.utils.LogUtils;
import cn.dawnland.packdownload.utils.MessageUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 重试拦截器
 */
public class RetryIntercepter implements Interceptor {

    /**
     * 最大重试次数
     */
    public int maxRetry;
    /**
     * 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
     */
    private int retryNum = 0;

    public RetryIntercepter(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain){
        Request request = chain.request();
        Response response = null;
        try{
            response = chain.proceed(request);
            Thread.sleep(10000);
        }catch (Exception e){
            LogUtils.info(e.getMessage());
        }
        while (response == null || (!response.isSuccessful() && retryNum < maxRetry)) {
            retryNum++;
            System.out.println("retryNum=" + retryNum + request.url());
            try {
                response = chain.proceed(request);
            } catch (IOException e) {
                LogUtils.info(e.getMessage());
            }
        }
        return response;
    }


}
