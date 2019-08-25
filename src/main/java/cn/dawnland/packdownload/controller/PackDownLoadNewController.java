package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.model.Project;
import cn.dawnland.packdownload.task.ModPackZipDownLoadTask;
import cn.dawnland.packdownload.utils.CurseUtils;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackDownLoadNewController implements Initializable {

    @FXML private AnchorPane root;
    @FXML private Label downloadSpeed;
    @FXML private Label resultLabel;
    @FXML private JFXButton downloadButton;
    @FXML private JFXTextField threadCount;
    @FXML private JFXTextField projectUrlTextField;
    //    @FXML private Hyperlink copyrightHyperlink;
//    @FXML private Button opinionButton;
    @FXML private JFXButton selectDirButton;
    @FXML private JFXTextField searchText;
    @FXML private JFXButton searchButton;
    //    @FXML private Button projectNameSearchButton;
    @FXML private HBox searchHbox;
    @FXML private CheckBox divideVersionCheckBox;
    @FXML private JFXListView<HBox> taskList;
    private static JFXTextField projectUrlTextFieldStatic;
    private static HBox seartchHboxStatic;

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

    public void searchPack() throws IOException {
        String searchStr = searchText.getText();
        Set<Project> projects = CurseUtils.searchProjectByName(searchStr);
        if(projects.size() < 1){
            MessageUtils.info("请确认后重新搜索", "未搜索到整合包");
            return;
        }
        ObservableList obs = FXCollections.observableArrayList();
        projects.forEach(p -> obs.add(p.getName()+ " - " + p.getUrl()));
        JFXComboBox comboBox = new JFXComboBox<>();
        comboBox.setPromptText("请选择一个整合包.....");
        searchHbox.getChildren().removeAll(searchText, searchButton);
        comboBox.setPrefWidth(searchHbox.getWidth());
        comboBox.setItems(obs);
        comboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue)
                        -> projectUrlTextField.setText(comboBox.getValue().toString().split(" - ")[1]));
        searchHbox.getChildren().add(comboBox);
    }

    public void startPackDownLoad(){
        UIUpdateUtils.startButton = downloadButton;
        projectUrlTextFieldStatic = projectUrlTextField;
        seartchHboxStatic = searchHbox;
        Integer threadCount = 10;
        String projectUrl = projectUrlTextField.getText();
        if (projectUrlTextField.getText() == null && "".equals(projectUrlTextField.getText())) {
            MessageUtils.info("请输入整合包链接");
            return;
        }
        if(this.threadCount.getText() != null && !this.threadCount.getText().equals("")){
            try{
                threadCount = Integer.valueOf(this.threadCount.getText());
            }catch (Exception e){
                Platform.runLater(() -> resultLabel.setText("线程数只能为整数"));
                return;
            }
        }
        if(!projectUrlTextField.getText().startsWith("http://www.curseforge.com/minecraft/modpacks") && !projectUrlTextField.getText().startsWith("https://www.curseforge.com/minecraft/modpacks")){
            MessageUtils.error("整合包链接错误", "请输入正确的整合包链接");
            return;
        }
        String title = CurseUtils.getDocumentByProjectUrl(projectUrl).getElementsByClass("font-bold text-lg break-all").text();
        title = title.split(" - ")[0];
        if(divideVersionCheckBox.isSelected()){
            DownLoadUtils.setPackPath(DownLoadUtils.getPackPath() + "/versions/" + DownLoadUtils.filenameFilter(title));
        }
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        pool.submit(new ModPackZipDownLoadTask(null, projectUrl, taskList, pool));
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
        if(seartchHboxStatic != null && projectUrlTextFieldStatic != null){
            seartchHboxStatic.setDisable(true);
            projectUrlTextFieldStatic.setDisable(true);
        }
    }
}
