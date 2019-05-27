package main;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.FxmlUtils;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(getClass().getResource("/fxml/PackDownLoad.fxml"));
        Parent root = FxmlUtils.LoadFxml("PackDownLoad.fxml");
        primaryStage.setTitle("整合包下载器");
        primaryStage.setScene(new Scene(root, 450, 270));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
