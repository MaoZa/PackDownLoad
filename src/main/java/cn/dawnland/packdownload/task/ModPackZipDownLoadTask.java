package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.controller.PackDownLoadNewController;
import cn.dawnland.packdownload.utils.*;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Cap_Sub
 */
public class ModPackZipDownLoadTask implements Runnable {

    private ExecutorService pool;

    private String zipFilePath;
    private JFXListView taskList;

    public ModPackZipDownLoadTask(String zipFilePath, JFXListView taskList, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.taskList = taskList;
        this.pool = pool;
    }

    @Override
    public void run() {
        PackDownLoadNewController.setDisplay();
        MessageUtils.info("正在解析整合包ZIP...");
        pool.submit(new JsonJXTask(zipFilePath, taskList, pool));
    }
}
