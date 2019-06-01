package main;

import configs.Config;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.DownLoadUtils;
import utils.FxmlUtils;
import utils.MessageUtils;
import utils.Upgrader;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        showPackDownLoad(primaryStage);
    }

    public void showPackDownLoad(Stage primaryStage) throws IOException {
        //System.out.println(getClass().getResource("/fxml/PackDownLoad.fxml"));
        //检查更新 是否弹出版本提示框
        //MessageUtils.info(Upgrader.isNewVersion() + "");
        if(Upgrader.isNewVersion()){
            Upgrader.versionLog();
        }
        //自动更新
        //如果version.json 永远比当前版本高 实现每次打开强制更新
        Upgrader.autoupgrade();

        // 为按钮添加事件——点击时打开新的窗口
//        Button opinionButton = new Button("反馈");
//        opinionButton.setLayoutX(396);
//        opinionButton.setLayoutY(14);
//        opinionButton.setOnMouseClicked(event -> {
//            Parent root = null;
//            try {
//                root = FxmlUtils.LoadFxml("Opinion.fxml");
//            } catch (IOException e) {
//                MessageUtils.error(e);
//            }
//            primaryStage.setTitle("反馈");
//            primaryStage.setScene(new Scene(root, 400, 200));
//            primaryStage.initModality(Modality.APPLICATION_MODAL);
//            primaryStage.show();
//        });

        Parent root = FxmlUtils.LoadFxml("PackDownLoad.fxml");
//        ((AnchorPane)root).getChildren().add(opinionButton);
        primaryStage.setTitle("整合包下载器");
        primaryStage.setScene(new Scene(root, 450, 300));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        try{
            launch(args);
        }catch (Exception e){
            MessageUtils.error(e);
        }
    }
}
