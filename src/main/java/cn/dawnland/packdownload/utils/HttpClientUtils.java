package cn.dawnland.packdownload.utils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class HttpClientUtils {

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
            public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) {
                if((httpResponse.getStatusLine().getStatusCode()+"").startsWith("3")){
                    return true;
                }
                return false;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) {
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
}
