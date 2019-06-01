package task;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import utils.MessageUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Cap_Sub
 */
public class UnZipTask implements Runnable{

    private String location;
    private ZipEntry ze;
    private ProgressBar progressBar;
    private List<Integer> cs;
    private Double proSize;
    private Label proLable;

    private FileOutputStream fout;

    public UnZipTask(String location, ZipEntry ze, ProgressBar progressBar, List<Integer> cs, Double proSize, Label proLable) {
        this.location = location;
        this.ze = ze;
        this.progressBar = progressBar;
        this.cs = cs;
        this.proSize = proSize;
        this.proLable = proLable;
    }

    @Override
    public void run() {
        try {
            String path = location + "\\" + ze.getName();
            path = path.replaceFirst("overrides", ".minecraft");
            if (ze.isDirectory()) {
                File unzipFile = new File(path);
                if(!unzipFile.isDirectory()) {
                    unzipFile.mkdirs();
                }
            }
            else {
                fout = new FileOutputStream(path, false);
                cs.forEach(c -> {
                    try {
                        fout.write(c);
                    } catch (IOException e) {
                        MessageUtils.error(e);
                        e.printStackTrace();
                    }
                });
            }
        }catch (Exception e){

        }finally {
            Platform.runLater(() -> {
                progressBar.setProgress(progressBar.getProgress() + proSize);
                String[] split = proLable.getText().split("/");
                proLable.setText(Integer.valueOf(split[0]) + 1 + "/" + split[1]);
                if((Integer.valueOf(split[0]) + 1) == Integer.valueOf(split[1])){
                    Label resultLabel = MessageUtils.resultLabel;
                    Label downloadSpeed = MessageUtils.downloadSpeed;
                    if(downloadSpeed.getText().equals("下载完成") || resultLabel.getText().equals("下载完成")){
                        resultLabel.setText("安装完成");
                    }else{
                        resultLabel.setText("解压完成");
                    }
                }
            });
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    MessageUtils.error(e);
                    e.printStackTrace();
                }
            }
        }
    }
}
