package cn.dawnland.packdownload.utils;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cap_Sub
 */
public class UIUpdateUtils {

    public static Button startButton;

    private static ProgressBar modsBar;
    private static ProgressBar unzipBar;
    private static Label modsLabel;
    private static Label unzipLabel;
    private static int modsCount;
    private static int unzipCount;
    public static JFXListView<HBox> taskList;

    public static AtomicInteger modsPoint = new AtomicInteger(0);
    public static AtomicInteger unzipPoint = new AtomicInteger(0);

    public static void initMods(HBox hb, ProgressBar modsBar, Label modsLabel, int modsCount){
        UIUpdateUtils.modsBar = modsBar;
        UIUpdateUtils.modsLabel = modsLabel;
        UIUpdateUtils.modsCount = modsCount;
        ((Runnable) () -> {
            while(true){
                updateLable(modsLabel, modsPoint.get() + "/" + modsCount);
                updateProgress(modsBar, ((1D/modsCount) * modsPoint.get()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(modsPoint.get() == modsCount){
                    updateLable(modsLabel, modsPoint.get() + "/" + modsCount);
                    updateProgress(modsBar, ((1D/modsCount) * modsPoint.get()));
                    MessageUtils.info("Mod下载完成,请等待其他任务完成...");
                    MessageUtils.setStatus();
//                    taskList.getItems().remove(hb);
                    break;
                }
            }
        }).run();
    }

    public static void initUnzip(HBox hb, ProgressBar unzipBar, Label unzipLabel, int unzipCount){
        UIUpdateUtils.unzipBar = unzipBar;
        UIUpdateUtils.unzipLabel = unzipLabel;
        UIUpdateUtils.unzipCount = unzipCount;
        ((Runnable) () -> {
            while(true){
                updateLable(unzipLabel, unzipPoint.get() + "/" + unzipCount);
                updateProgress(unzipBar, ((1D / unzipCount) * unzipPoint.get()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(unzipPoint.get() == unzipCount){
                    updateLable(unzipLabel, unzipPoint.get() + "/" + unzipCount);
                    updateProgress(unzipBar, ((1D / unzipCount) * unzipPoint.get()));
                    MessageUtils.setStatus();
                    MessageUtils.info("解压完成,请等待其他任务完成...");
//                    taskList.getItems().remove(hb);
                    break;
                }
            }
        }).run();
    }

    public static void modsBarAddOne(){
        modsPoint.addAndGet(1);
    }

    public static void unzipBarAddOne(){
        unzipPoint.addAndGet(1);
    }

    public static String encode(String souStr){
        try {
            return new String(souStr.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void updateProgress(ProgressBar progressBar, double point){
        Platform.runLater(() -> progressBar.setProgress(point));
    }

    public static synchronized void updateProgress(ProgressBar progressBar, int count){
        Platform.runLater(() -> progressBar.setProgress(progressBar.getProgress() + (1D/count)));
    }

    public static synchronized void updateLable(Label label, String values){
        Platform.runLater(() -> label.setText(values));
    }

}
