package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;

import com.alibaba.fastjson.JSONObject;
import configs.Config;

import javax.swing.*;

import static java.util.regex.Pattern.*;

/**
 * @author Cap_Sub
 */
public class Upgrader {
    /** 最新版本号 */
    public static float newVersion;
    /** 下载完成与否 */
    public static boolean downloaded = false;
    /**下载出错与否 */
    public static boolean errored = false;
    /** 新版本更新信息 */
    public static String description = "";

    static {
        getNewVersion();
    }

    /**
     * 对url中文进行UrlEncode编码
     *
     * @param url
     * @return
     */
    public static String urlEncodeChinese(String url) {
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

    /**
     * 静默下载最新版本
     */
    public static void dowload() throws UnsupportedEncodingException {
        downLoadFromUrl(Config.exeUrl, "dowloadtmp", "tmp");
        downLoadFromUrl(Config.batUrl, "update.bat", DownLoadUtils.getRootPath());
        downloaded = true;
    }

    /**
     * 重启完成更新
     */
    public static void restart() {
        try {
            Runtime.getRuntime().exec("cmd /k start .\\update.bat");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最新版本号
     */
    public static void getNewVersion() {
        String json = sendGetRequest(Config.versinUrl);
        JSONObject ob = JSONObject.parseObject(json);
        newVersion = ob.getFloat("version");
        description = ob.getString("desc");
    }

    /**
     * 启动后自动更新
     */
    public static void autoupgrade() {
        getNewVersion();
        if (Config.currentVersion >= newVersion) {
            return;
        }
        try {
            dowload();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        restart();
    }

    /**
     * 检查是否有新版本
     *
     * @return
     */
    public static boolean isNewVersion() {
        if (newVersion == 0) {
            getNewVersion();
        }
        if (newVersion > Config.currentVersion) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 发get请求，获取文本
     *
     * @param getUrl
     * @return 网页context
     */
    public static String sendGetRequest(String getUrl) {
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr;
        BufferedReader br;
        try {
            URL url = new URL(getUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setAllowUserInteraction(false);
            isr = new InputStreamReader(url.openStream(), "UTF-8");
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            // 获取自己数组
            byte[] getData = readInputStream(inputStream);

            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

            System.out.println("info:" + url + " download success");
        } catch (Exception e) {
            MessageUtils.error(e);
            System.exit(0);
        }

    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}

