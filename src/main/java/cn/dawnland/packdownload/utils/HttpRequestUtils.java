package cn.dawnland.packdownload.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;

public class HttpRequestUtils {

    private String errorMessage; // 錯誤信息
    /**
     * HTTP請求字符串資源
     *
     * @param url
     *      URL地址
     * @return 字符串資源
     * */
    public String httpRequestString(String url) {
        String result = null;
        try {
            HttpEntity httpEntity = httpRequest(url);
            if (httpEntity != null) {
                result = EntityUtils.toString(httpEntity, "urf-8"); // 使用UTF-8編碼
            }
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }
        return result;
    }
    /**
     * HTTP請求字節數組資源
     *
     * @param url
     *      URL地址
     * @return 字節數組資源
     * */
    public byte[] httpRequestByteArray(String url) {
        byte[] result = null;
        try {
            HttpEntity httpEntity = httpRequest(url);
            if (httpEntity != null) {
                result = EntityUtils.toByteArray(httpEntity);
            }
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }
        return result;
    }

    public static InputStream getInputStream4Url(String url) throws IOException {
        url = HttpClientUtils.urlEncode(url);
        HttpEntity httpEntity = new HttpRequestUtils().httpRequest(url);
        if(httpEntity != null){
            return httpEntity.getContent();
        }
        return null;
    }

    /**
     * 获取文件大小
     * @param url
     * @return
     */
    public static long getSize4Url(String url){
        HttpEntity httpEntity = new HttpRequestUtils().httpRequest(url);
        if(httpEntity != null) {
            return httpEntity.getContentLength();
        }
        return 0;
    }

    /**
     * 使用GET方式請求
     *
     * @param url
     *      URL地址
     * @return HttpEntiry對象
     * */
    private HttpEntity httpRequest(String url) {
        HttpEntity result = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = HttpClientUtils.getHttpClient();
            HttpResponse httpResponse;
            httpResponse = httpClient.execute(httpGet);
            int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
            if(httpStatusCode == HttpStatus.SC_TEMPORARY_REDIRECT){
                String location = httpResponse.getFirstHeader("location").getValue();
                if(location == null){
                    return null;
                }
                httpResponse = httpClient.execute(new HttpPost(location));
            }
            /*
             * 判斷HTTP狀態碼是否為200
             */
            if (httpStatusCode == HttpStatus.SC_OK) {
                result = httpResponse.getEntity();
            } else {
                errorMessage = "HTTP: " + httpStatusCode;
            }
        } catch (ClientProtocolException e) {
            errorMessage = e.getMessage();
        } catch (IOException e) {
            errorMessage = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 返回錯誤消息
     *
     * @return 錯誤信息
     * */
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
