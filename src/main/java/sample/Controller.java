package sample;

import com.alibaba.fastjson.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.ModDownLoad;
import utils.DownLoadUtils;

import javax.swing.*;
import java.io.IOException;

public class Controller {

    @FXML private TextField packCode;
    @FXML private Button button;

    @FXML
    public void onDownLoad(ActionEvent event) throws IOException {
        ModDownLoad modDownLoad = new ModDownLoad();
        modDownLoad.setName("[等价交换]ProjectE (by sinkillerj)");
        modDownLoad.setVersion("PE1.4.1");
        modDownLoad.setUrl("https://minecraft.curseforge.com/projects/projecte/files/2702991/download");
        modDownLoad.setMcVersion(1122);
        modDownLoad.setForgeVersion(1144282L);
        modDownLoad.setFileSize(153664L);
        DownLoadUtils.downLoadFile(modDownLoad.getUrl(), modDownLoad.getFileName(), "mods", modDownLoad.getFileSize());

        JOptionPane.showMessageDialog(null, JSONObject.toJSON(modDownLoad));
    }

}
