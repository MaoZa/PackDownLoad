package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.configs.Config;
import cn.dawnland.packdownload.model.ForgeVersion;
import javafx.application.Platform;
import javafx.scene.control.Label;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cap_Sub
 * @version 创建时间：2019/05/30 14:42
 */
public class MessageUtils {

    public static Label resultLabel;
    public static AtomicInteger sizeAI = new AtomicInteger(0);
    private static ExecutorService pool = Executors.newFixedThreadPool(1);
    public static Label downloadSpeed;

    public static int status = 0;

    public static Boolean isOk(){
        return status == 2 ? true : false;
    }

    public synchronized static void setStatus(){
        status = status + 1;
    }

    public static void error(Exception e){
        Platform.runLater(() -> {
            JOptionPane.showMessageDialog(null, "未知错误", e.getStackTrace().toString(), 0);
            UIUpdateUtils.startButton.setDisable(false);
        });
        LogUtils.error(e);
        e.printStackTrace();
    }

    public static void error(String msg, String title){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg, title, 0));

    }

    public static void info(String msg, String title){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg, title, 1));
//        Platform.runLater(() -> resultLabel.setText(title + ":" + msg));
    }

    public static void info (String msg){
//        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg));
        Platform.runLater(() -> resultLabel.setText(msg));
    }

    public static void downloadSpeedStart(){
        pool.submit((Runnable) () -> {
            while (true) {
                int size = sizeAI.get();
                try {
                    Thread.sleep(1000);
                    int speed = sizeAI.get() - size;
                    if(speed > 0 && downloadSpeed != null){
                        UIUpdateUtils.updateLable(downloadSpeed, readableFileSize(speed) + "/s");
                        sizeAI.set(0);
                    }else if(downloadSpeed != null){
                        UIUpdateUtils.updateLable(downloadSpeed, "0kb/s");
                    }
                } catch (InterruptedException e) {
                    MessageUtils.error(e);
                }
            }
        });
    }

    public static String readableFileSize(long size) {
        if (size <= 0) { return "0"; }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void endPool(){
        pool.shutdown();
    }

}
