package cn.dawnland.packdownload.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @author Cap_Sub
 */
public class DownLoadUtils {


    public static ConcurrentMap<String, String> downloadFaildModS = new ConcurrentHashMap();

    public static JFXListView taskList;

    private static Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");
    public static String filenameFilter(String str) {
        return str==null?null:FilePattern.matcher(str).replaceAll("");
    }

    public static Label downloadSpeed;
    private static String rootPath;
    private static String packPath;
    private static String downloadServerUrl = "http://localhost:8099/oss?url=";

    static {
        rootPath = DownLoadUtils.getRootPath();
    }

    public static String getPackPath(){
        if(packPath == null){
            packPath = getRootPath() + "/.minecraft";
        }
        return packPath;
    }

    public static void setPackPath(String path){
        packPath = path;
    }

    /**
     * 下载文件到指定目录 不指定path则下载到rootPath下
     * @param url 下载链接
     */
    public static String downLoadFile(String url, String path, OkHttpUtils.OnDownloadListener onDownloadListener) throws IOException {
        if(path == null || "".equals(path)){
            path = getPackPath();
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DownLoadUtils.downLoadFromUrl(url, path, onDownloadListener);
    }

    /**
     * 下载整合包文件
     * @param url 下载链接
     * @param fileName 保存文件的文件名
     * @param path 相对于.minecraft的路径(.minecraft = 根目录)
     */
    public static boolean downLoadMod(String url, String fileName, String path, OkHttpUtils.OnDownloadListener onDownloadListener) throws IOException {
        if(path == null || "".equals(path)){
            path = rootPath;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        DownLoadUtils.downLoadModFromUrl(url, fileName, path, onDownloadListener);
        return true;
    }

    /**
     * 下载整合包文件
     * @param url 下载链接
     * @param fileName 保存文件的文件名
     * @param path 相对于.minecraft的路径(.minecraft = 根目录)
     */
    public static boolean downLoadFile(String url, String fileName, String path, Long size) throws IOException {
        path = rootPath + "/" + path;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        //如果传入文件size 则判断本地是否已经下载该文件且大小一致(则不下载直接返回)
        if(size != null && size > 0){
            File localFile = new File(path + "/" + fileName);
            if(localFile.exists() && size.equals(localFile.length())){
                return false;
            }
        }
        DownLoadUtils.downLoadFromUrl(url, fileName, path);
        return true;
    }


    /**
     * 获取运行目录
     * @return
     */
    public static String getRootPath(){
        if(rootPath == null){
            return System.getProperty("user.dir");
        }
        return rootPath;
    }

    public static void setRootPath(String path){
        rootPath = path;
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        fileName = new String((conn.getURL().getPath().substring(conn.getURL().getPath().lastIndexOf("/") + 1)).getBytes(), "UTF-8");
        long fileSize = Long.valueOf(conn.getHeaderField("Content-Length"));

        File saveDir = new File(savePath);
        String path = saveDir + File.separator + fileName;
        File file = new File(path);

        //如果传入文件size 则判断本地是否已经下载该文件且大小一致(则不下载直接返回)
        if(fileSize > 0){
            if(file.exists() && fileSize == file.length()){
                System.out.println(fileName + "已存在:大小" + fileSize);
                return;
            }
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadModFromUrl(String urlStr, String fileName, String savePath, OkHttpUtils.OnDownloadListener onDownloadListener) {
        OkHttpUtils.get().download(urlStr, savePath, onDownloadListener);
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);;
            MessageUtils.sizeAI.set(MessageUtils.sizeAI.get() + len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 从网络Url中下载文件到指定目录 通用方法
     * @param urlStr
     * @param savePath
     * @throws IOException
     */
    public static String downLoadFromUrl(String urlStr, String savePath, OkHttpUtils.OnDownloadListener onDownloadListener) throws IOException {
        OkHttpUtils.get().download(downloadServerUrl + urlStr, savePath, onDownloadListener);
        return null;
    }

    public static void downloadVersionJson(String mcVersion, String forgeVersion, String installUrl) throws IOException {
        MessageUtils.info("正在安装核心...");
        String s = OkHttpUtils.get().get(downloadServerUrl + MojangUtils.getJsonUrl(mcVersion));
        JSONObject jsonObject = JSONObject.parseObject(s);
        String jarName = "";
        try {
            MessageUtils.info("正在下载Forge...");
            DownLoadUtils.downLoadFromUrl(downloadServerUrl + installUrl, DownLoadUtils.getPackPath(), new OkHttpUtils.OnDownloadListener() {

                final Label modsLabel = new Label();
                final JFXProgressBar modsBar = new JFXProgressBar();
                final Label lable = new Label();
                final HBox modsHb = new HBox();

                private boolean flag = false;

                @Override
                public void onDownloadSuccess(File file) throws IOException {

                    Platform.runLater(() -> {
                        if(modsHb.getParent() != null){
                            UIUpdateUtils.taskList.getItems().remove(modsHb);
                        }
                    });

                    String filename = file.getName();
                    MessageUtils.info("正在安装Forge...");
                    File universal = ZipUtils.getZipEntryFile(file.getPath(), filename.replaceFirst("installer", "universal"));
                    File versionJsonFile = ZipUtils.getZipEntryFile(universal.getPath(), "version.json");
                    String versionJson = FileUtils.readJsonData(versionJsonFile.getPath());
                    JSONObject versionObject = JSONObject.parseObject(versionJson);
                    JSONArray libraries = (JSONArray) versionObject.get("libraries");
                    libraries.addAll((JSONArray)jsonObject.get("libraries"));
                    jsonObject.put("libraries", libraries);
                    jsonObject.put("minecraftArguments", versionObject.get("minecraftArguments"));
                    jsonObject.put("mainClass", versionObject.get("mainClass"));
                    DownLoadUtils.downLoadFromUrl("https://dawnland.cn/hmclversion.cfg", DownLoadUtils.getPackPath(), new OkHttpUtils.OnDownloadListener() {

                        final Label modsLabel = new Label();
                        final JFXProgressBar modsBar = new JFXProgressBar();
                        final Label lable = new Label();
                        final HBox modsHb = new HBox();

                        private boolean flag = false;

                        @Override
                        public void onDownloadSuccess(File file) {
                            Platform.runLater(() -> {
                                if(modsHb.getParent() != null){
                                    UIUpdateUtils.taskList.getItems().remove(modsHb);
                                }
                            });
                            String tempStr = packPath.substring(packPath.lastIndexOf("/") + 1);
                            File f = new File(packPath + "/" + (".minecraft".equals(tempStr) ? "versions/" + mcVersion + "/" + mcVersion + ".json" : tempStr + ".json"));
                            if(!f.isDirectory()) {
                                File tempF = new File(f.getPath().substring(0, f.getPath().lastIndexOf("\\")));
                                tempF.mkdirs();
                                f = new File(packPath + "/" + (".minecraft".equals(tempStr) ? "versions/" + mcVersion + "/" + mcVersion + ".json" : tempStr + ".json"));
                            }
                            FileOutputStream fio = null;
                            try {
                                fio = new FileOutputStream(f);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            PrintStream ps = new PrintStream(fio);
                            ps.print(jsonObject.toJSONString());
                            ps.close();
                            try {
                                fio.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MessageUtils.info("安装完成");
                        }

                        @Override
                        public void onDownloading(int progress, String filename) {
                            if(!flag){
                                modsHb.setPrefWidth(350D);
                                modsHb.setSpacing(10D);
                                modsHb.setAlignment(Pos.CENTER);
                                modsBar.setPrefWidth(70D);
                                modsBar.setMaxHeight(5D);
                                modsBar.setProgress(0);
                                modsLabel.setText(filename);
                                modsLabel.setPrefWidth(150D);
                                modsLabel.setMaxHeight(5);
                                modsLabel.setAlignment(Pos.CENTER_LEFT);
                                lable.setPrefWidth(30D);
                                lable.setAlignment(Pos.CENTER_RIGHT);
                                Platform.runLater(() -> {
                                    modsHb.getChildren().addAll(modsLabel, modsBar, lable);
                                    DownLoadUtils.taskList.getItems().add(modsHb);
                                });
                                flag = true;
                            }
                            Platform.runLater(() -> {
                                lable.setText(progress + "%");
                                modsBar.setProgress(progress / 100D);
                            });
                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });
                }

                @Override
                public void onDownloading(int progress, String filename) {
                    if(!flag){
                        modsHb.setPrefWidth(350D);
                        modsHb.setSpacing(10D);
                        modsHb.setAlignment(Pos.CENTER);
                        modsBar.setPrefWidth(70D);
                        modsBar.setMaxHeight(5D);
                        modsBar.setProgress(0);
                        modsLabel.setText(filename);
                        modsLabel.setPrefWidth(150D);
                        modsLabel.setMaxHeight(5);
                        modsLabel.setAlignment(Pos.CENTER_LEFT);
                        lable.setPrefWidth(30D);
                        lable.setAlignment(Pos.CENTER_RIGHT);
                        Platform.runLater(() -> {
                            modsHb.getChildren().addAll(modsLabel, modsBar, lable);
                            DownLoadUtils.taskList.getItems().add(modsHb);
                        });
                        flag = true;
                    }
                    Platform.runLater(() -> {
                        lable.setText(progress + "%");
                        modsBar.setProgress(progress / 100D);
                    });
                }

                @Override
                public void onDownloadFailed(Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void isOpenLanauch(Label resultLabel){
        JOptionPane.showMessageDialog(null, "打开启动器开始玩耍吧!", "安装完成", 1);
        CommonUtils.appExit();

//        resultLabel.setText("安装完成");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("安装完成");
//        alert.setHeaderText("");
//        alert.setContentText("是否打开启动器");
//        Optional result = alert.showAndWait();
//        if (result.get() == ButtonType.OK) {
//            /** 执行cmd命令打开启动器 */
//            try {
//                Desktop.getDesktop().open(new File(DownLoadUtils.getRootPath() + "/黎明大陆伪正版启动器.exe"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.exit(0);
//        }
    }

}
