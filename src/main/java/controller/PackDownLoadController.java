package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import task.ModPackZipDownLoadTask;
import utils.DownLoadUtils;
import utils.MessageUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class PackDownLoadController implements Initializable{

    @FXML private AnchorPane root;
    @FXML private Label downloadSpeed;
    @FXML private BorderPane progressPane;
    @FXML private Label resultLabel;
    @FXML private Button startPackDownLoad;
    @FXML private TextField threadCount;
    @FXML private TextField projectUrlTextField;
//    @FXML private Hyperlink copyrightHyperlink;
//    @FXML private Button opinionButton;
    @FXML private Button selectDirButton;
    @FXML private TextField projectNameSearchText;
    @FXML private Button projectNameSearchButton;
    @FXML private HBox seartchHbox;
    private String baseUrl = "https://www.curseforge.com";

    private String projectUrl;

    public void searchPack() throws IOException {
        String searchText = projectNameSearchText.getText();
        String searchUrl = baseUrl + "/minecraft/modpacks/" +
                "search?search=";
        searchUrl += searchText;
        Document document = Jsoup.connect(searchUrl).get();
        Elements elementsByClass = document.getElementsByClass("list-item__details xs-mg-r-1");
        if(elementsByClass.size() < 1){
            MessageUtils.info("请确认后重新搜索", "未搜索到整合包");
            return;
        }
        ConcurrentMap<String, Object> projectMap = new ConcurrentHashMap<>(elementsByClass.size());
        ObservableList obs = FXCollections.observableArrayList();
        elementsByClass.forEach(e -> {
            Elements a = e.getElementsByTag("a");
            projectMap.put(a.get(0).getElementsByTag("h2").text(), baseUrl + a.attr("href"));
            obs.add(a.get(0).getElementsByTag("h2").text() + "@" + baseUrl + a.get(0).attr("href"));
        });
        ComboBox comboBox = new ComboBox<>();
        comboBox.setPromptText("请选择一个整合包.....");
        seartchHbox.getChildren().remove(1);
        comboBox.setPrefWidth(seartchHbox.getWidth());
        comboBox.setItems(obs);
        comboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue)
                        -> projectUrlTextField.setText(comboBox.getValue().toString().split("@")[1]));
        int i = seartchHbox.getChildren().indexOf(projectNameSearchText);
        seartchHbox.getChildren().set(i, comboBox);

    }

    public void selectedDir(){
        Stage stage = (Stage) progressPane.getParent().getScene().getWindow();
        if(stage != null){
            DirectoryChooser dc = new DirectoryChooser();
            File file = dc.showDialog(stage);
            DownLoadUtils.setRootPath(file.getPath());
            Platform.runLater(() -> selectDirButton.setText(file.getPath()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DownLoadUtils.downloadSpeed = downloadSpeed;
        MessageUtils.resultLabel = resultLabel;
        MessageUtils.downloadSpeed = downloadSpeed;
        MessageUtils.downloadSpeedStart();
    }

    public void openUrlByCopyright(){
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/MaoZa"));
        } catch (Exception e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }

    public void startPackDownLoad(){
        seartchHbox.setDisable(true);
        projectUrlTextField.setDisable(true);
        Integer threadCount = 10;
        projectUrl = projectUrlTextField.getText();
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
        try {
            Jsoup.connect(projectUrl).get();
        } catch (Exception e) {
            MessageUtils.error("不存在或无法访问", "整合包链接错误");
            e.printStackTrace();
            return;
        }
        projectUrl = projectUrlTextField.getText();
        Platform.runLater(() -> {
            resultLabel.setText("请稍等,正在下载整合包Zip...");
            startPackDownLoad.setText("正在安装");
            startPackDownLoad.setDisable(true);
        });
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        pool.submit(new ModPackZipDownLoadTask(null, projectUrl, progressPane, pool));
    }

}
