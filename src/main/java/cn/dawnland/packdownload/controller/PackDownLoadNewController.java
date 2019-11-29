package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.task.ModPackZipDownLoadTask;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author Cap_Sub
 */
public class PackDownLoadNewController implements Initializable {

    @FXML private AnchorPane root;
    @FXML private Label downloadSpeed;
    @FXML private Label resultLabel;
    @FXML private JFXButton downloadButton;
    @FXML private JFXTextField threadCount;
    @FXML private JFXTextField projectUrlTextField;
    @FXML private JFXButton selectDirButton;
    @FXML private CheckBox divideVersionCheckBox;
    @FXML private JFXListView<HBox> taskList;
    @FXML private JFXButton selectZipDirButton;
    @FXML private JFXButton installButton;
    @FXML private HBox targetHbox;
    private static File zipFile;
    private static JFXTextField projectUrlTextFieldStatic;
    private static HBox targetHboxStatic;
//    public static ExecutorService initPool = newFixedThreadPool(5);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        threadCount.setPromptText("线程数:默认10");
        DownLoadUtils.taskList = taskList;
        divideVersionCheckBox.setSelected(true);
        MessageUtils.downloadSpeed = downloadSpeed;
        MessageUtils.resultLabel = resultLabel;
        UIUpdateUtils.taskList = taskList;
    }

    public void selectedDir(){
        Stage stage = (Stage) resultLabel.getParent().getScene().getWindow();
        if(stage != null){
            DirectoryChooser dc = new DirectoryChooser();
            File file = dc.showDialog(stage);
            DownLoadUtils.setRootPath(file.getPath());
            Platform.runLater(() -> selectDirButton.setText(file.getPath()));
        }
    }

    public void selectedZipDir(){
        Stage stage = (Stage) resultLabel.getParent().getScene().getWindow();
        if(stage != null){
            final HBox modsHb = new HBox();
            FileChooser fc = new FileChooser();
            zipFile = fc.showOpenDialog(stage);
            selectZipDirButton.setText(zipFile.getName());
        }
    }

    public void startPackDownLoad(){
        UIUpdateUtils.startButton = downloadButton;
        projectUrlTextFieldStatic = projectUrlTextField;
        targetHboxStatic = targetHbox;
        Integer threadCount = 10;
        if(this.threadCount.getText() != null && !this.threadCount.getText().equals("")){
            try{
                threadCount = Integer.valueOf(this.threadCount.getText());
            }catch (Exception e){
                Platform.runLater(() -> resultLabel.setText("线程数只能为整数"));
                return;
            }
        }
        if(divideVersionCheckBox.isSelected()){
            DownLoadUtils.setPackPath(DownLoadUtils.getPackPath() + "/versions/" + zipFile.getName().split(".zip")[0]);
        }
        ExecutorService pool = newFixedThreadPool(threadCount);
        pool.submit(new ModPackZipDownLoadTask(zipFile.getPath(), taskList, pool));
        Platform.runLater(() -> {
            root.setMaxWidth(root.getMaxWidth() + 400D);
            root.setMinWidth(root.getMaxWidth());
            root.setPrefWidth(root.getMaxWidth());
        });
        MessageUtils.info("请稍等,正在下载整合包Zip...");
        Platform.runLater(() -> {
            downloadButton.setText("正在安装");
            downloadButton.setDisable(true);
        });
    }

    public static void setDisplay(){
        if(targetHboxStatic != null && projectUrlTextFieldStatic != null){
            targetHboxStatic.setDisable(true);
            projectUrlTextFieldStatic.setDisable(true);
        }
    }
}
