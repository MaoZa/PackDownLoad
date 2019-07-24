package cn.dawnland.packdownload.utils;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class HttpConUtils {

    public static HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setInstanceFollowRedirects(false);
        return connection;
    }

    public static HttpURLConnection resolveConnection(HttpURLConnection conn) throws IOException {
        int redirect = 0;
        while (true) {

            conn.setUseCaches(false);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);
            Map<String, List<String>> properties = conn.getRequestProperties();
            int code = conn.getResponseCode();
            if (code >= 300 && code <= 307 && code != 306 && code != 304) {
                String newURL = conn.getHeaderField("Location");
                conn.disconnect();

                if (redirect > 20) {
                    throw new IOException("Too much redirects");
                }

                HttpURLConnection redirected = (HttpURLConnection) new URL(conn.getURL(), encodeLocation(newURL)).openConnection();
                properties.forEach((key, value) -> value.forEach(element -> redirected.addRequestProperty(key, element)));
                conn = redirected;
                ++redirect;
            } else {
                break;
            }
        }
        return conn;
    }

    // ==== Shortcut methods for encoding/decoding URLs in UTF-8 ====
    public static String encodeURL(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error();
        }
    }

    /**
     * @see <a href="https://github.com/curl/curl/blob/3f7b1bb89f92c13e69ee51b710ac54f775aab320/lib/transfer.c#L1427-L1461">Curl</a>
     * @param location
     * @return
     */
    public static String encodeLocation(String location) {
        StringBuilder sb = new StringBuilder();
        boolean left = true;
        for (char ch : location.toCharArray()) {
            switch (ch) {
                case ' ':
                    if (left) sb.append("%20");
                    else sb.append('+');
                    break;
                case '?':
                    left = false;
                default:
                    if (ch >= 0x80)
                        sb.append(encodeURL(Character.toString(ch)));
                    else
                        sb.append(ch);
                    break;
            }
        }

        return sb.toString();
    }

}
