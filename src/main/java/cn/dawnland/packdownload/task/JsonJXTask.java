package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.configs.Config;
import cn.dawnland.packdownload.listener.DownloadListener;
import cn.dawnland.packdownload.model.curse.CurseModInfo;
import cn.dawnland.packdownload.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Cap_Sub
 */
public class JsonJXTask implements Runnable {

    private ExecutorService pool;

    private String jsonPath;
    private String zipFilePath;
    private JFXListView taskList;

    public JsonJXTask(String zipFilePath, JFXListView taskList, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.taskList = taskList;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            if (jsonPath == null) {
                try{
                    jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").getPath();
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
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");
            String mcVersion = ((Map)jsonObject.get("minecraft")).get("version") + "";
            String forgeVersionStr = (((Map)((List)((Map)jsonObject.get("minecraft")).get("modLoaders")).get(0)).get("id") + "");
            ForgeUtils.downloadForgeNew(mcVersion, forgeVersionStr);

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getPackPath(), taskList, pool);
            Iterator<JSONObject> iterator = files.iterator();

            //获取已成功下载的文件(mod)列表
            File successModFile = new File(DownLoadUtils.getRootPath() + "/successMod.txt");
            if(successModFile.exists()){
                BufferedReader br = new BufferedReader(new FileReader(successModFile));
                String s = null;
                while ((s = br.readLine())!=null){
                    String[] split = s.split(":");
                    successModMap.put(split[0], split[1]);
                }
            }

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
                label.setText("0/" + (files.size() - successModMap.size()));
                MessageUtils.info("正在安装整合包，请耐心等待");
                Platform.runLater(() -> {
                    hb.getChildren().addAll(modsLabel, modsBar, label);
                    DownLoadUtils.taskList.getItems().add(hb);
                });
                UIUpdateUtils.modsBar = modsBar;
                UIUpdateUtils.modsLabel = label;
                UIUpdateUtils.modsCount = (files.size() - successModMap.size());
            });
            ((Runnable)() -> {
                while (iterator.hasNext()){
                    JSONObject object = iterator.next();
                    String projectID = object.get("projectID").toString();
                    String fileId = object.get("fileID").toString();
                    if(successModMap.get(projectID) != null && successModMap.get(projectID).equals(fileId)){
                        continue;
                    }
                    request(object);
                }
            }).run();
        } catch (Exception e) {
            MessageUtils.error(e);
        }

    }

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    private String baseUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/%s/file/%s";
    private String addonUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/%s/file/%s/download-url";

    private Map<String, String> successModMap = new HashMap<>();

    public void request(JSONObject jsonObject) {
        pool.submit(() -> {
            String projectId = jsonObject.get("projectID").toString();
            String fileId = jsonObject.get("fileID").toString();
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
            CurseModInfo curseModInfo = new CurseModInfo();
            try {
                curseModInfo.setDownloadUrl(OkHttpUtils.get().get(String.format(addonUrl, projectId, fileId)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            curseModInfo.setDisplayName(projectId + ":" + fileId);
            new ModDownLoadTask(curseModInfo, MODS_PATH).subTask();
        });
    }
}
