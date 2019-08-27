package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.controller.PackDownLoadNewController;
import cn.dawnland.packdownload.utils.*;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Cap_Sub
 */
public class ModPackZipDownLoadTask implements Runnable {

    private ExecutorService pool;

    private String baseUrl = "https://www.curseforge.com";
    private String projectUrl;
    private String fileUrl;

    private String zipFilePath;
    private JFXListView taskList;

    public ModPackZipDownLoadTask(String baseUrl, String projectUrl, JFXListView taskList,
                                  ExecutorService pool) {
        this.baseUrl = baseUrl == null ? this.baseUrl : baseUrl;
        this.projectUrl = projectUrl;
        this.taskList = taskList;
        this.pool = pool;
    }

    @Override
    public void run() {
        if(projectUrl.indexOf("/files") == -1){
            projectUrl = projectUrl + "/files";
        }
        ConcurrentHashMap<String, String> resultMap = CurseUtils.getProjectNameAndDownloadUrl(projectUrl);
        MessageUtils.downloadSpeedStart();
        DownLoadUtils.downLoadFile(resultMap.get("downloadUrl"), null, new OkHttpUtils.OnDownloadListener() {

            final Label modsLabel = new Label();
            final JFXProgressBar modsBar = new JFXProgressBar();
            final Label lable = new Label();
            final HBox modsHb = new HBox();

            private boolean flag = false;

            @Override
            public void onDownloadSuccess(File file) throws IOException {
                PackDownLoadNewController.setDisplay();
                MessageUtils.info("下载整合包Zip成功 正在解析...");
                pool.submit(new JsonJXTask(file.getPath(), taskList, pool));
                UIUpdateUtils.taskList.getItems().remove(modsHb);
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
                    lable.setAlignment(Pos.CENTER_RIGHT);
                    lable.setPrefWidth(30D);
                    lable.setAlignment(Pos.CENTER_LEFT);
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
                e.printStackTrace();
            }
        });
    }
}
