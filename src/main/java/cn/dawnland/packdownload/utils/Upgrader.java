package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.configs.Config;
import cn.dawnland.packdownload.listener.DownloadListener;
import cn.dawnland.packdownload.task.DownloadTask;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author Cap_Sub
 */
public class Upgrader {

    /** 最新版本号 */
    public static float newVersion;
    /** 下载完成与否 */
    public static boolean downloaded = false;
    public static AtomicInteger tag = new AtomicInteger(0);
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
    public static String urlEncode(String url) {
        try {
            Matcher matcher = compile("[\\u4e00-\\u9fa5]|\\[|\\]").matcher(url);
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

    public static void versionLog() {
        //提示框只展示最近三次更新的内容
        String versionLog = Upgrader.description;
        Pattern pt = compile("(\\d+.\\d+)");
        Matcher mt = pt.matcher(versionLog);
        Map<String, String[]> versionMap = new LinkedHashMap<>();
        List<Integer> indexs = new ArrayList<>();
        while(mt.find()){
            indexs.add(mt.start());
        }
        for (Integer index : indexs) {
            if(indexs.indexOf(index) + 1 != indexs.size()){
                String temp = versionLog.substring(index, indexs.get(indexs.indexOf(index) + 1));
                String[] temps = temp.split(":");
                String[] split = temps[1].split("\\n");
                versionMap.put(temps[0], split);
            }else {
                String temp = versionLog.substring(index);
                String[] temps = temp.split(":");
                String[] split = temps[1].split("\\n");
                versionMap.put(temps[0], split);
            }
        }
        String versionShowStr = "更新内容:\n";
        int i = 0;
        for (Map.Entry<String, String[]> e : versionMap.entrySet()) {
            //限制提示框只显示最近三次更新的日志
            if(i < 3){
                versionShowStr += e.getKey() + ":\n";
                for (String s : e.getValue()) {
                    if(!"".equals(s)){
                        versionShowStr += "    " + s + "\n";
                    }
                }
                i++;
            }
        }

        ((Runnable) () -> {
            //更新日志写入文件
            //记录完整更新日志 需异步
            File file = new File(DownLoadUtils.getRootPath() + "/更新日志.txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                MessageUtils.error(e);
            }
            PrintStream ps = new PrintStream(fos);
            for (Map.Entry<String, String[]> e : versionMap.entrySet()) {
                ps.println(e.getKey() + ":");
                for (String s : e.getValue()) {
                    if(!"".equals(s)){
                        ps.println("\t" + s);
                    }
                }
            }
        }).run();
        JOptionPane.showMessageDialog(null, versionShowStr, "发现新版本 " + Upgrader.newVersion, 1);
    }

    /**
     * 下载最新版本
     */
    public static void download() {
        downLoadFromUrl(Config.batUrl, new DownloadListener() {
                    @Override
                    public void onSuccess(File file) {
                        downLoadFromUrl(Config.exeUrl, DownLoadUtils.getRootPath() + File.separator + "tmp", new DownloadListener() {
                            @Override
                            public void onSuccess(File file) {
                                JOptionPane.showConfirmDialog(null, "点击确定重启软件", "下载成功", JOptionPane.PLAIN_MESSAGE);
                                restart();
                            }
                            @Override
                            public void onFailed(String filename, String url) {
                                super.onFailed(filename, url);
                                MessageUtils.error("仍无法更新可联系作者或前往https://dawnland.cn/PackDownload下载最新版", "自动更新失败，请检查网络后重试。");
                            }
                        });
                    }
        });
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
        String json = sendGetRequest(Config.versionUrl);
        JSONObject ob = JSONObject.parseObject(json);
        try{
            newVersion = ob.getFloat("version");
            description = ob.getString("desc");
        }catch (Exception e){
            LogUtils.error(new Date() + ": 获取更新信息失败");
            LogUtils.error(e);
        }
    }


    /**
     * 启动后自动更新
     */
    public static void autoUpgrade() {
        getNewVersion();
        if (Config.currentVersion >= newVersion) {
            return;
        }
        try {
            download();
        } catch (Exception e) {
            MessageUtils.error(e);
            System.exit(0);
        }
        while(true){
            if(tag.get() == 2){
                restart();
            }
        }
    }

    /**
     * 检查是否有新版本
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
            isr = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            MessageUtils.error(e);
        }
        return sb.toString();
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String savePath, DownloadListener listener) {
        if(Objects.isNull(savePath) || "".equals(savePath.trim())){
            savePath = DownLoadUtils.getRootPath();
        }
        new DownloadTask(savePath, listener).startDownload(urlStr);
    }
    public static void downLoadFromUrl(String urlStr, DownloadListener listener) {
        String savePath = DownLoadUtils.getRootPath();
        new DownloadTask(savePath, listener).startDownload(urlStr);
    }
}

