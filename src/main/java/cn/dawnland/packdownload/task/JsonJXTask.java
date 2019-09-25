package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.netty.config.NettyConfig;
import cn.dawnland.packdownload.netty.packet.request.DownloadRequestPacket;
import cn.dawnland.packdownload.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").getPath();
            }
            String fileJson = FileUtils.readJsonData(jsonPath);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");
            String mcVersion = ((Map)jsonObject.get("minecraft")).get("version") + "";
            String forgeVersion = ((Map)((List)((Map)jsonObject.get("minecraft")).get("modLoaders")).get(0)).get("id") + "";
            ForgeUtils.downloadForge(mcVersion, forgeVersion);

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getPackPath(), taskList, pool);
            //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
            //                                                     项目id        文件id
            Iterator<JSONObject> iterator = files.iterator();
            pool.submit(() -> {
                MessageUtils.info("正在下载启动器...");
                try {
                    Upgrader.downLoadFromUrl("https://dawnland.cn/" + URLEncoder.encode("黎明大陆伪正版启动器", "UTF-8") + ".exe", "黎明大陆伪正版启动器.exe" , "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
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
                label.setPrefWidth(60D);
                label.setAlignment(Pos.CENTER_RIGHT);
                MessageUtils.info("正在下载所需文件，请耐心等待");
                Platform.runLater(() -> {
                    hb.getChildren().addAll(modsLabel, modsBar, label);
                    DownLoadUtils.taskList.getItems().add(hb);
                });
                pool.submit(()-> UIUpdateUtils.initMods(hb, modsBar, label, files.size()));
            });
            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                request(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    private String baseUrl = "https://www.curseforge.com/minecraft/mc-mods/%s/download/%s/file";

    public void request(JSONObject jsonObject){
        String projectId = jsonObject.get("projectID").toString();
        String fileId = jsonObject.get("fileID").toString();
        String url = String.format(baseUrl, projectId, fileId);
        NettyConfig.request(new DownloadRequestPacket(url, MODS_PATH));
    }

}
