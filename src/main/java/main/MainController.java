package main;

import com.alibaba.fastjson.JSONArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import model.DownLoadModel;
import model.ModDownLoad;
import task.DownLoadTask;
import utils.HttpUtils;
import utils.UIUpdateUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Deprecated
/**
 * 弃用
 */
public class MainController {

    @FXML private TextField packCode;
    //下载按钮
    @FXML private Button download;
    //日志框
    @FXML private TextArea logText;
    //百分比
    @FXML private Label percentage;
    //进度条
    @FXML private ProgressBar progressBar;
    //下载线程数
    @FXML private TextField threadField;
    private List<ModDownLoad> downLoadModels;

    public void onDownLoad(ActionEvent event) throws IOException, InterruptedException {

        logText.setText("");

        String code = packCode.getText();
        String resultStr = HttpUtils.get("http://localhost:8089/downList/get?code=" + code);
        try{
            downLoadModels = JSONArray.parseArray(resultStr, ModDownLoad.class);
            UIUpdateUtils.textAreaAppend(logText, "获取文件列表成功（共" + downLoadModels.size() + "个文件）\n");
            percentage.setText("0.0%");
            progressBar.setProgress(0);
        }catch (Exception e){
            UIUpdateUtils.updateLable(percentage, "");
            return;
        }
        if(downLoadModels.size() < 0){
            UIUpdateUtils.textAreaAppend(logText, "modList为空\n");
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool("".equals(threadField.getText()) ? 5 : Integer.valueOf(threadField.getText()));
        Iterator<ModDownLoad> iterator = downLoadModels.iterator();
        while (iterator.hasNext()){
            DownLoadModel downLoadModel = iterator.next();
            pool.execute(new DownLoadTask(downLoadModel, this));
        }


    }

}
