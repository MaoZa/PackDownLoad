package cn.dawnland.packdownload.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequestUtils {

    private String errorMessage; // 錯誤信息


    public static InputStream getInputStream4Url(String url) throws IOException {
        url = HttpClientUtils.urlEncode(url);
        HttpEntity httpEntity = new HttpRequestUtils().httpRequest(url);
        if(httpEntity != null){
            return httpEntity.getContent();
        }
        return null;
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

}
