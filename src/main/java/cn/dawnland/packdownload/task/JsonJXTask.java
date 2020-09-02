package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.configs.Config;
import cn.dawnland.packdownload.listener.DownloadListener;
import cn.dawnland.packdownload.model.manifest.Manifest;
import cn.dawnland.packdownload.model.manifest.ManifestFile;
import cn.dawnland.packdownload.utils.*;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author Cap_Sub
 */
public class JsonJXTask implements Runnable {

    private ExecutorService pool;

    private Path jsonPath;
    private String zipFilePath;
    private JFXListView taskList;

    public static Manifest manifest;

    public JsonJXTask(String zipFilePath, JFXListView taskList, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.taskList = taskList;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            jsonPath = Paths.get(DownLoadUtils.getPackPath(), "manifest.json");
            if (!jsonPath.toFile().exists()) {
                try{
                    jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").toPath();
                }catch (Exception e){
                    MessageUtils.error("不支持的整合包ZIP", "异常");
                    MessageUtils.info("不支持的整合包ZIP");
                    Thread.currentThread().stop();
                }
            }
            MessageUtils.downloadSpeedStart();
            pool.submit(() -> {
                MessageUtils.info("正在下载启动器...");
                Upgrader.downLoadFromUrl(Config.lancherUrl, "", new DownloadListener() {});
                Upgrader.downLoadFromUrl(Config.authlibInjectorsUrl, "", new DownloadListener() {});
            });
            String fileJson = FileUtils.readJsonData(jsonPath);
            try{
                manifest = JSONObject.parseObject(fileJson, Manifest.class);
            }catch (JSONException je){
                try{
                    jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").toPath();
                    fileJson = FileUtils.readJsonData(jsonPath);
                }catch (Exception e){
                    MessageUtils.error("不支持的整合包ZIP", "异常");
                    MessageUtils.info("不支持的整合包ZIP");
                    Thread.currentThread().stop();
                }
                manifest = JSONObject.parseObject(fileJson, Manifest.class);
            }
            manifest.setThisJsonFilePath(jsonPath.toString());

            ZipUtils.unzip(manifest, zipFilePath, DownLoadUtils.getPackPath(), taskList, pool);
            List<ManifestFile> files = manifest.getFiles();

            Platform.runLater(() -> {
                JFXProgressBar modsBar = new JFXProgressBar();
                Label modsLabel = new Label("下载进度");
                modsBar.setPrefWidth(70);
                HBox hb = new HBox();
                Label label = new Label();
                hb.setPrefWidth(350D);
                hb.setSpacing(10D);
                hb.setAlignment(Pos.CENTER);
                modsBar.setPrefWidth(130D);
                modsBar.setMaxHeight(5D);
                modsBar.setProgress(0);
                modsLabel.setPrefWidth(60D);
                modsLabel.setMaxHeight(5);
                modsLabel.setAlignment(Pos.CENTER_LEFT);
                label.setPrefWidth(100D);
                label.setAlignment(Pos.CENTER_RIGHT);
                Long processedQuantity = manifest.getFiles().stream().filter(f -> !f.isDownloadSucceed()).count();
                label.setText("0/" + processedQuantity);
                MessageUtils.info("正在安装整合包，请耐心等待");
                Platform.runLater(() -> {
                    hb.getChildren().addAll(modsLabel, modsBar, label);
                    DownLoadUtils.taskList.getItems().add(hb);
                });
                UIUpdateUtils.modsBar = modsBar;
                UIUpdateUtils.modsLabel = label;
                UIUpdateUtils.modsCount = files.size() - (files.size() - processedQuantity.intValue());
            });
            Set<ManifestFile> processedFiles = manifest.getFiles().stream().filter(f -> !f.isDownloadSucceed()).collect(Collectors.toSet());
            processedFiles.forEach(this::request);
            String mcVersion = manifest.getMinecraft().getVersion();
            String forgeVersionStr = manifest.getMinecraft().getModLoaders().get(0).getId();
            ForgeUtils.downloadForgeNew(mcVersion, forgeVersionStr);
        } catch (Exception e) {
            MessageUtils.error(e);
        }

    }

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";
//    private String baseUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/%s/file/%s";
    private final String ADDON_URL = "https://addons-ecs.forgesvc.net/api/v2/addon/%s/file/%s/download-url";

    public void request(ManifestFile manifestFile) {
        pool.submit(() -> {
//            String url = String.format(baseUrl, projectId, fileId);
//            CurseModInfo curseModInfo = null;
//            try {
//                curseModInfo = JSONObject.parseObject(OkHttpUtils.get().get(url), CurseModInfo.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Path path = Paths.get(MODS_PATH + File.separator + curseModInfo.getFileName());
//            if(Files.exists(path)){
//                File file = new File(path.toString());
//                if(file.length() == curseModInfo.getFileLength()){
//                    UIUpdateUtils.modsBarAddOne();
//                    LogUtils.info(file.getName() + "已下载{跳过}");
//                    return;
//                }
//            }
            try {
                if(manifestFile.getDownloadUrl() == null || manifestFile.getDownloadUrl().trim().length() < 1){
                    manifestFile.setDownloadUrl(OkHttpUtils.get().get(String.format(ADDON_URL, manifestFile.getProjectID(), manifestFile.getFileID())));
                }
            } catch (IOException e) {
                MessageUtils.error(e);
            }
            manifestFile.setDisName(manifestFile.getProjectID() + ":" + manifestFile.getFileID());
            new ModDownLoadTask(manifest, manifestFile, MODS_PATH).subTask();
        });
    }
}
