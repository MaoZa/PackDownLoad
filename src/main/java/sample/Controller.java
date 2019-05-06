package sample;

import com.alibaba.fastjson.JSONArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.ModDownLoad;
import task.DownLoadTask;
import utils.HttpUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    @FXML private TextField packCode;
    @FXML private Button button;
    @FXML private TextArea logText;

    @FXML
    public void onDownLoad(ActionEvent event) throws IOException {

        String code = packCode.getText();
        String resultStr = HttpUtils.get("http://localhost:8089/downList/get?code=" + code);
        List<ModDownLoad> downLoadModels = null;
        try{
            downLoadModels = null;
            downLoadModels = JSONArray.parseArray(resultStr, ModDownLoad.class);
            logText.appendText("获取文件列表成功（共" + downLoadModels.size() + "个文件）\n");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, new String(resultStr.getBytes(), "UTF-8"));
            JOptionPane.showMessageDialog(null, e.getStackTrace());
            return;
        }
        if(downLoadModels.size() < 0){
            logText.appendText("modList为空");
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (ModDownLoad downLoadModel : downLoadModels) {
            pool.execute(new DownLoadTask(downLoadModel, logText));
        }


    }

}
