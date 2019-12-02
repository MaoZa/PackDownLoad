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

    private static String rootPath;
    private static String packPath;

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
     * 下载整合包文件
     * @param url 下载链接
     * @param path 相对于.minecraft的路径(.minecraft = 根目录)
     */
    public static boolean downLoadMod(String url, String path, OkHttpUtils.OnDownloadListener onDownloadListener) {
        if(path == null || "".equals(path)){
            path = rootPath;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        DownLoadUtils.downLoadModFromUrl(url, path, onDownloadListener);
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
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadModFromUrl(String urlStr, String savePath, OkHttpUtils.OnDownloadListener onDownloadListener) {
        OkHttpUtils.get().download(urlStr, savePath, onDownloadListener);
    }

    /**
     * 从网络Url中下载文件到指定目录 通用方法
     * @param urlStr
     * @param savePath
     * @throws IOException
     */
    public static String downLoadFromUrl(String urlStr, String savePath, OkHttpUtils.OnDownloadListener onDownloadListener) {
        OkHttpUtils.get().download(urlStr, savePath, onDownloadListener);
        return null;
    }

    public static void downloadVersionJson(String mcVersion, String forgeVersion, String installUrl) throws IOException {
        MessageUtils.info("正在安装核心,获取Mojang配置中...");
        String s = OkHttpUtils.get().get(MojangUtils.getJsonUrl(mcVersion));
        JSONObject jsonObject = JSONObject.parseObject(s);
        MessageUtils.info("正在下载Forge...");
        DownLoadUtils.downLoadFromUrl(installUrl, DownLoadUtils.getPackPath(), new OkHttpUtils.OnDownloadListener() {

            final Label modsLabel = new Label();
            final JFXProgressBar modsBar = new JFXProgressBar();
            final Label lable = new Label();
            final HBox modsHb = new HBox();

            private boolean flag = false;

            @Override
            public void onDownloadSuccess(File file){
                Platform.runLater(() -> {
                    if(modsHb.getParent() != null){
                        UIUpdateUtils.taskList.getItems().remove(modsHb);
                    }
                });
                MessageUtils.info("Forge下载完成,正在安装Forge...");
                File versionJsonFile = null;
                try {
                    versionJsonFile = ZipUtils.getZipEntryFile(file.getPath(), "version.json");
                } catch (IOException e) {
                    MessageUtils.error(e);
                }
                String versionJson = FileUtils.readJsonData(versionJsonFile.getPath());
                JSONObject versionObject = JSONObject.parseObject(versionJson);
                JSONArray libraries = (JSONArray) versionObject.get("libraries");
                libraries.addAll((JSONArray)jsonObject.get("libraries"));
                MessageUtils.info("正在添加依赖");
                jsonObject.put("libraries", libraries);
                jsonObject.put("minecraftArguments", versionObject.get("minecraftArguments"));
                jsonObject.put("mainClass", versionObject.get("mainClass"));
                MessageUtils.info("正在下载配置文件...");
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
