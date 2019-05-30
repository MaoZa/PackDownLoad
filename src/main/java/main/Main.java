package main;

import configs.Config;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.FxmlUtils;
import utils.MessageUtils;
import utils.Upgrader;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //System.out.println(getClass().getResource("/fxml/PackDownLoad.fxml"));
        //检查更新 是否弹出版本提示框
        //MessageUtils.info(Upgrader.isNewVersion() + "");
        if(Upgrader.isNewVersion()){
            MessageUtils.info("更新内容:\n" + Upgrader.description, "发现新版本");
        }

        //自动更新
        //如果version.json 永远比当前版本高 实现每次打开强制更新
        Upgrader.autoupgrade();

        Parent root = FxmlUtils.LoadFxml("PackDownLoad.fxml");
        primaryStage.setTitle("整合包下载器");
        primaryStage.setScene(new Scene(root, 450, 270));
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
