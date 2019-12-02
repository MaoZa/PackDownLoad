package cn.dawnland.packdownload.utils;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cap_Sub
 */
public class UIUpdateUtils {

    public static Button startButton;

    public static ProgressBar modsBar;
    public static ProgressBar unzipBar;
    public static Label modsLabel;
    public static Label unzipLabel;
    public static int modsCount;
    public static int unzipCount;
    public static JFXListView<HBox> taskList;

    public static AtomicInteger modsPoint = new AtomicInteger(0);
    public static AtomicInteger unzipPoint = new AtomicInteger(0);

    public static void modsBarAddOne(){
        modsPoint.addAndGet(1);
        Platform.runLater(() -> {
            updateLable(modsLabel, modsPoint.get() + "/" + modsCount);
            updateProgress(modsBar, ((1D/modsCount) * modsPoint.get()));
            if(modsPoint.get() == modsCount){
                Platform.runLater(() -> {
                    MessageUtils.info("Mod下载完成,请等待其他任务完成...");
                    MessageUtils.setStatus();
                });
            }
        });
    }

    public static void unzipBarAddOne(){
        unzipPoint.addAndGet(1);
        Platform.runLater(() -> {
            updateLable(unzipLabel, unzipPoint.get() + "/" + unzipCount);
            updateProgress(unzipBar, ((1D / unzipCount) * unzipPoint.get()));
            if(unzipPoint.get() == unzipCount){
                MessageUtils.setStatus();
                MessageUtils.info("解压完成,请等待其他任务完成...");
                return;
            }
        });
    }

    public static synchronized void updateProgress(ProgressBar progressBar, double point){
        Platform.runLater(()-> progressBar.setProgress(point));
    }

    public static synchronized void updateLable(Label label, String values){
        Platform.runLater(() -> label.setText(values));
    }

}
