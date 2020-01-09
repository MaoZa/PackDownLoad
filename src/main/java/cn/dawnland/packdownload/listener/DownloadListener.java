package cn.dawnland.packdownload.listener;

import cn.dawnland.packdownload.utils.DownLoadUtils;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;

/**
 * @author Created by cap_sub@dawnland.cn
 */
public abstract class DownloadListener{

    public final Label titleLabel = new Label();
    public final JFXProgressBar modsBar = new JFXProgressBar();
    public final Label barlabel = new Label();
    public final HBox hb = new HBox();
    public String defaultText;
    private boolean flag = false;

    public DownloadListener() {
        init();
    }

    public DownloadListener(String defaultText) {
        this.defaultText = defaultText;
        init();
    }

    private void init(){
        hb.setPrefWidth(360D);
        hb.setSpacing(10D);
        hb.setAlignment(Pos.CENTER);
        modsBar.setPrefWidth(70D);
        modsBar.setMaxHeight(5D);
        modsBar.setProgress(0);
        titleLabel.setText("解析中:" + (defaultText == null || "".equals(defaultText) ? Thread.currentThread().getName() : defaultText));
        titleLabel.setPrefWidth(150D);
        titleLabel.setMaxHeight(5);
        barlabel.setAlignment(Pos.CENTER_RIGHT);
        barlabel.setPrefWidth(40D);
        barlabel.setAlignment(Pos.CENTER_LEFT);
        Platform.runLater(() -> {
            hb.getChildren().addAll(titleLabel, modsBar, barlabel);
            DownLoadUtils.taskList.getItems().add(hb);
        });
    }

    /**
     * 下载完成通知
     * 如果重写此方法则必须在方法首行执行super.onSuccess(file)
     * @param file
     */
    public void onSuccess(File file){
        this.removeHb();
    }

    /**
     * 下载进度通知
     */
    public void onProgress(int progress, String filename){
        if(!flag){

            flag = true;
        }
        Platform.runLater(() -> {
            titleLabel.setText("下载中:" + filename);
            barlabel.setText(progress + "%");
            modsBar.setProgress(progress / 100D);
        });
    }

    /**
     * 下载失败通知
     */
    public void onFailed(String filename, String url){
        System.out.println("下载失败:" + filename + "-" + url);
        DownLoadUtils.downloadFaildModS.put(filename, url);
    }

    /**
     * 下载暂停通知
     */
    public void onPaused(){
        Platform.runLater(() -> barlabel.setText("下载暂停"));
    }

    /**
     * 下载取消通知
     */
    public void onCanceled(){
        this.removeHb();
    }

    private void removeHb(){
        if(DownLoadUtils.taskList.getItems().contains(this.hb)){
            Platform.runLater(() -> DownLoadUtils.taskList.getItems().remove(this.hb));
        }
    }
}
