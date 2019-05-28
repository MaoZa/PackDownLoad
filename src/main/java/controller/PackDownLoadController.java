package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import task.ModPackZipDownLoadTask;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackDownLoadController {

    @FXML private BorderPane progressPane;
    @FXML private Label resultLabel;
    @FXML private Button startPackDownLoad;
    @FXML private TextField threadCount;
    @FXML private TextField projectUrlTextField;

//    private String zipFilePath = "D:\\PackDownLoad\\src\\main\\resources\\SkyFactory4-4.0.5.zip";
//    private String projectUrl = "https://www.curseforge.com/minecraft/modpacks/skyfactory-4";
    private String projectUrl;

    public void startPackDownLoad(){
        Platform.runLater(() -> {
            resultLabel.setText("请稍等...");
            ((HBox)startPackDownLoad.getParent()).getChildren().remove(startPackDownLoad);
        });
        if (projectUrlTextField.getText() != null) {
            projectUrl = projectUrlTextField.getText();
        }else{
            Platform.runLater(() -> {
                JOptionPane.showMessageDialog(null, "请输入整合包链接");
            });
            return;
        }
        Integer threadCount = 10;
        if(this.threadCount.getText() != null){
            try{
                threadCount = Integer.valueOf(this.threadCount.getText());
            }catch (Exception e){ }
        }
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        pool.submit(new ModPackZipDownLoadTask(null, projectUrl, progressPane, pool));
    }




}
