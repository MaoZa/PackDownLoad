package task;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utils.DownLoadUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @author Cap_Sub
 */
public class ModPackZipDownLoadTask implements Runnable {

    private ExecutorService pool;

    private String baseUrl = "https://www.curseforge.com";
    private String projectUrl;
    private String fileUrl;

    private String zipFilePath;
    private BorderPane progressPane;

    public ModPackZipDownLoadTask(String baseUrl, String projectUrl, BorderPane progressPane,
                                  ExecutorService pool) {
        this.baseUrl = baseUrl == null ? this.baseUrl : baseUrl;
        this.projectUrl = projectUrl;
        this.progressPane = progressPane;
        this.pool = pool;
    }

    @Override
    public void run() {
        if(projectUrl.indexOf("/files") == -1){
            projectUrl = projectUrl + "/files";
        }
        Document document = null;
        try {
            document = Jsoup.connect(projectUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elementsByClass = document.getElementsByClass("button button--twitch download-button");
        if(elementsByClass.size() < 1){
            Platform.runLater(() -> {
                JOptionPane.showMessageDialog(null, "获取整合包文件失败，请确认链接是否正确");
            });
            return;
        }else{
            fileUrl = baseUrl + elementsByClass.get(0).getElementsByTag("a").attr("href");
            fileUrl = fileUrl.substring(0, fileUrl.length() - 9) + "/file";
        }

        boolean b = false;
        try {
            Platform.runLater(() -> {
                JOptionPane.showMessageDialog(null, "正在下载整合包Zip...");
            });
            String fileName = DownLoadUtils.downLoadFile(fileUrl, null);
            zipFilePath = DownLoadUtils.getRootPath() + "/" + fileName;
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(b){
            Platform.runLater(() -> {
                JOptionPane.showMessageDialog(null, "下载整合包Zip成功 正在解析...");
            });

            pool.submit(new JsonJXTask(zipFilePath, progressPane, pool));
        }
    }
}
