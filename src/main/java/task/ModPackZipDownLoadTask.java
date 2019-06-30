package task;

import controller.PackDownLoadController;
import javafx.scene.layout.BorderPane;
import utils.CurseUtils;
import utils.DownLoadUtils;
import utils.MessageUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
        ConcurrentHashMap<String, String> resultMap = CurseUtils.getProjectNameAndDownloadUrl(projectUrl);
        boolean b = false;
        try {
            String fileName = DownLoadUtils.downLoadFile(resultMap.get("downloadUrl"), null);
            zipFilePath = DownLoadUtils.getPackPath() + "/" + fileName;
            b = true;
        } catch (IOException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        if(b){
            PackDownLoadController.setDisplay();
            MessageUtils.info("下载整合包Zip成功 正在解析...");
            pool.submit(new JsonJXTask(zipFilePath, progressPane, pool));
        }
    }
}
