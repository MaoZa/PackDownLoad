package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.model.ForgeVersion;
import cn.dawnland.packdownload.utils.*;
import com.alibaba.fastjson.JSONObject;
import cn.dawnland.packdownload.configs.Config;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.*;
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
    private BorderPane progressPane;

    public JsonJXTask(String zipFilePath, BorderPane progressPane, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.progressPane = progressPane;
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

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getPackPath(), progressPane, pool);

            Platform.runLater(() -> {
                Label modsLabel = new Label();
                ProgressBar modsBar = new ProgressBar();
                modsBar.setPrefWidth(230D);
                modsBar.setProgress(0);
                Label lable = new Label("下载进度");
                lable.setPrefWidth(20D);
                lable.setAlignment(Pos.CENTER_RIGHT);
                lable.setPrefWidth(50D);
                lable.setAlignment(Pos.CENTER_LEFT);
                HBox modsHb = new HBox();
                modsHb.setPrefWidth(300D);
                modsHb.setSpacing(5D);
                modsHb.setAlignment(Pos.CENTER);
                modsHb.getChildren().addAll(lable, modsBar, modsLabel);
                progressPane.setTop(modsHb);
                MessageUtils.info("下载中...");
                pool.submit(()-> UIUpdateUtils.initMods(modsBar, modsLabel, files.size()));
            });
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
            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                pool.submit(new FilesDownLoadTask(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
