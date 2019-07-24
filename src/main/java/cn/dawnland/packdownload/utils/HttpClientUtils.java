package cn.dawnland.packdownload.utils;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class HttpClientUtils {
    private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

    public static HttpClient getHttpsClient() throws Exception {
        CloseableHttpClient client = null;
        if (client != null) {
            return client;
        }
        SSLContext sslcontext = SSLContexts.custom().build();
        sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        client = HttpClients.custom().setSSLSocketFactory(factory)
                .setConnectionReuseStrategy(((httpResponse, httpContext) -> false))
                .build();

        return client;
    }

    // static ParseXml parseXML = new ParseXml();
    // 初始化HttpClient
    public static HttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = SSLContexts.custom().build();
        sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        HttpClientBuilder builder = HttpClients.custom().setSSLSocketFactory(factory);
        builder.setConnectionReuseStrategy(((httpResponse, httpContext) -> false));
        builder.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        builder.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                if((httpResponse.getStatusLine().getStatusCode()+"").startsWith("3")){
                    return true;
                }
                return false;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                String newUrl = httpResponse.getFirstHeader("location").getValue();
                newUrl = urlEncode(newUrl);
                HttpUriRequest httpUriRequest = new HttpGet(newUrl);
                return httpUriRequest;
            }
        });
        return builder.build();
    }

    /**
     * 对url中文进行UrlEncode编码
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        url = url.replace("[", "%5b");
        url = url.replace("]", "%5d");
        url = url.replace(" ", "%20");
        try {
            Matcher matcher = compile("[\\u4e00-\\u9fa5]").matcher(url);
            String tmp;
            while (matcher.find()) {
                tmp = matcher.group();
                url = url.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static boolean doPost(String url) throws Exception {
        HttpClient client = getHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response;
        response = client.execute(httppost);
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return true;
        }
        client.getConnectionManager().shutdown();
        return false;
    }
    /**
     * 与远程交互的返回值post方式
     *
     * @param hashMap
     * @param url
     * @return
     */
    public static String getHttpXml(HashMap<String, String> hashMap, String url) {
        String responseMsg = "";
        HttpPost request = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> iter = hashMap.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient client = HttpClientUtils.getHttpClient();
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                responseMsg = EntityUtils.toString(response.getEntity());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMsg;
    }
    /**
     * map转字符串 拼接参数
     *
     * @param hashMap
     * @return
     */
    public static String mapToString(HashMap<String, String> hashMap) {
        String parameStr = "";
        Iterator<Map.Entry<String, String>> iter = hashMap.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            parameStr += "&" + entry.getKey() + "=" + entry.getValue();
        }
        if (parameStr.contains("&")) {
            parameStr = parameStr.replaceFirst("&", "?");
        }
        return parameStr;
    }
}
