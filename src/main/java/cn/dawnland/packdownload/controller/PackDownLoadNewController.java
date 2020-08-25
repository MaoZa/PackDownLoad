package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.listener.DownloadListener;
import cn.dawnland.packdownload.model.curse.CurseProjectInfo;
import cn.dawnland.packdownload.task.ModPackZipDownLoadTask;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
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
    @FXML private HBox searchHbox;
    @FXML private TextField searchText;
    @FXML private Button searchButton;
    private static File zipFile;
    private static JFXTextField projectUrlTextFieldStatic;
    private static HBox targetHboxStatic;
    private static HBox searchHboxStatic;
    private static Button selectZipDirButtonStatic;
    private static Button startButtonStatic;
    private static Button selectDirButtonStatic;
    private static JFXTextField threadCountStatic;
    private static CheckBox divideVersionCheckBoxStatic;
    private static int count;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        threadCount.setPromptText("线程数:默认50");
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
            if(file != null){
                DownLoadUtils.setRootPath(file.getPath());
                Platform.runLater(() -> selectDirButton.setText(file.getPath()));
            }
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
        count += 1;
        divideVersionCheckBoxStatic = divideVersionCheckBox;
        threadCountStatic = threadCount;
        selectDirButtonStatic = selectDirButton;
        startButtonStatic = downloadButton;
        selectZipDirButtonStatic = selectZipDirButton;
        UIUpdateUtils.startButton = downloadButton;
        projectUrlTextFieldStatic = projectUrlTextField;
        targetHboxStatic = targetHbox;
        searchHboxStatic = searchHbox;
        startButtonStatic.setDisable(true);
        Integer threadCount = 50;
        if(this.threadCount.getText() != null && !this.threadCount.getText().equals("")){
            try{
                threadCount = Integer.valueOf(this.threadCount.getText());
            }catch (Exception e){
                Platform.runLater(() -> resultLabel.setText("线程数只能为整数"));
                return;
            }
        }
        ExecutorService pool = newFixedThreadPool(threadCount);
        if(divideVersionCheckBox.isSelected()){
            if(zipFile != null){
                if(count == 1){ DownLoadUtils.setPackPath(DownLoadUtils.getPackPath() + "/versions/" + zipFile.getName().split(".zip")[0]); }
                startInstall(pool);
            }else {
                String projectName = (String) ((ComboBox)searchHbox.getChildren().get(0)).getValue();
                String[] split = "\\,/,:,*,?,\",<,>,|".split(",");
                for (int i = 0; i < split.length; i++) {
                    projectName = projectName.replace(split[i], " ");
                }
                if(count == 1){ DownLoadUtils.setPackPath(DownLoadUtils.getPackPath() + "/versions/" + projectName); }
                pool.submit(() -> {
                    MessageUtils.info("正在下载整合包zip...");
                    DownLoadUtils.downLoadFromUrl(projectUrlTextField.getText(), DownLoadUtils.getPackPath(), new DownloadListener() {
                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            zipFile = file;
                            startInstall(pool);
                        }
                    });
                });
            }
        }


    }

    public static void setDisplay(){
        targetHboxStatic.setDisable(true);
        searchHboxStatic.setDisable(true);
        projectUrlTextFieldStatic.setDisable(true);
        selectZipDirButtonStatic.setDisable(true);
        selectDirButtonStatic.setDisable(true);
        threadCountStatic.setDisable(true);
        divideVersionCheckBoxStatic.setDisable(true);
    }

    public static void restart(){
        if(targetHboxStatic != null && projectUrlTextFieldStatic != null && searchHboxStatic != null){
            Platform.runLater(() -> {
                targetHboxStatic.setDisable(false);
                searchHboxStatic.setDisable(false);
                projectUrlTextFieldStatic.setDisable(false);
                startButtonStatic.setDisable(false);
                startButtonStatic.setText("开始下载");
                selectZipDirButtonStatic.setText("请选择整合包ZIP");
                selectZipDirButtonStatic.setDisable(false);
            });
        }
    }

    public void searchPack() throws IOException {
        String searchStr = searchText.getText();
        MessageUtils.info("正在搜索中,请稍后..可能会出现未响应请勿关闭软件");
        Map<String, Map<String, String>> projects = CurseProjectInfo.searchProject(searchStr);
        if(projects.size() < 1){
            MessageUtils.info("请确认后重新搜索", "未搜索到整合包");
            return;
        }
        MessageUtils.info("");
        targetHbox.getChildren().remove(0);
        ObservableList projectObs = FXCollections.observableArrayList();
        ObservableList fileObs = FXCollections.observableArrayList();
        projects.entrySet().stream().forEach(p -> projectObs.add(p.getKey()));
        JFXComboBox projectComboBox = new JFXComboBox<>();
        JFXComboBox latestComboBox = new JFXComboBox<>();
        projectComboBox.setPromptText("请选择一个整合包.....");
        searchHbox.getChildren().removeAll(searchText, searchButton);
        projectComboBox.setPrefWidth(searchHbox.getWidth());
        projectComboBox.setItems(projectObs);
        projectComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue)
                        -> {
                    latestComboBox.setPromptText("请选择一个版本.....");
                    fileObs.remove(0, fileObs.size());
                    projects.get(newValue).entrySet().stream().forEach(e -> fileObs.add(e.getKey()));
                    latestComboBox.setItems(fileObs);
                    latestComboBox.getSelectionModel()
                            .selectedItemProperty()
                            .addListener(((observable1, oldValue1, newValue1) -> {
                                projectUrlTextField.setText(projects.get(newValue).get(newValue1));
                            }));
                    if(targetHbox.getChildren().size() == 0){
                        targetHbox.getChildren().add(0, latestComboBox);
                    }
                });
        searchHbox.getChildren().add(projectComboBox);
    }

    private void startInstall(ExecutorService pool){
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

}
