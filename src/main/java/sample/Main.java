package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.FxmlUtils;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(getClass().getResource("/fxml/main.fxml"));
        Parent root = FxmlUtils.LoadFxml("main.fxml");
        primaryStage.setTitle("整合包下载器");
        primaryStage.setScene(new Scene(root, 800, 275 * 2));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
