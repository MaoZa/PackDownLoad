package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.LogUtils;
import cn.dawnland.packdownload.utils.OkHttpUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;

/**
 * @author Cap_Sub
 */
public class FilesDownLoadTask extends Task {

    private JSONObject jsonObject;

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    public FilesDownLoadTask(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    protected Object call() {
        return null;
    }

    private int reCount = 0;

    @Override
    public void run() {
        //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
        //                                                     项目id        文件id
        String url = "https://www.curseforge.com/minecraft/mc-mods/projectID/download/fileID/file";
        int count = 10;
        for (int i = reCount; i < count; i++) {
            try {
                String enter = "\n";
                String projectId = jsonObject.get("projectID").toString();
                String fileId = jsonObject.get("fileID").toString();
                url = url.replaceFirst("projectID", projectId);
                url = url.replaceFirst("fileID", fileId);
                DownLoadUtils.downLoadMod(url, projectId + "-" + fileId + ".jar", MODS_PATH, new OkHttpUtils.OnDownloadListener() {

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
                        UIUpdateUtils.modsBarAddOne();
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
                break;
            } catch (Exception e) {
                LogUtils.error(e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if((reCount + 1) == count){
                    System.out.println("reCount{}" + reCount);
                    /** 添加下载失败的mod入 **/
                    UIUpdateUtils.modsBarAddOne();
                    DownLoadUtils.downloadFaildModS.putIfAbsent(jsonObject.get("projectID") + "", jsonObject.get("fileID") + "");
                }else{
                    System.out.println(url + "{连接失败正在重试:"+ (reCount + 1) + "}");
                }
                reCount++;
            }
        }

    }






}
