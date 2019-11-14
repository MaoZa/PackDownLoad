package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.model.CurseModInfo;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.OkHttpUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;

public class ModDownLoadTask extends BaseTask<String> {

    private final CurseModInfo curseModInfo;
    private final String path;

    public ModDownLoadTask(Callback<String> callback, CurseModInfo curseModInfo, String path) {
        super(callback);
        this.curseModInfo = curseModInfo;
        this.path = path;
    }
    @Override
    void subTask() {
        DownLoadUtils.downLoadMod(curseModInfo.getDownloadUrl(), path, new OkHttpUtils.OnDownloadListener() {

            final Label modsLabel = new Label();
            final JFXProgressBar modsBar = new JFXProgressBar();
            final Label lable = new Label();
            final HBox modsHb = new HBox();

            private boolean flag = false;

            @Override
            public void onDownloadSuccess(File file) {
                Platform.runLater(() -> {
                    UIUpdateUtils.taskList.getItems().remove(modsHb);
                    UIUpdateUtils.modsBarAddOne();
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
                    modsLabel.setText(curseModInfo.getDisplayName());
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
