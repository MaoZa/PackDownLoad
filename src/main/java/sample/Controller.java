package sample;

import com.alibaba.fastjson.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.ModDownLoad;

import javax.swing.*;

public class Controller {

    @FXML private TextField packCode;
    @FXML private Button button;

    @FXML
    public void onDownLoad(ActionEvent event){
        ModDownLoad modDownLoad = new ModDownLoad();
        modDownLoad.setName("ttt");
        modDownLoad.setVersion("2.0");
        modDownLoad.setUrl("https://www.baidu.com");
        modDownLoad.setFileName("植物魔法" + modDownLoad.getVersion());

        JOptionPane.showMessageDialog(null, JSONObject.toJSON(modDownLoad));
    }

}
