package task;

import controller.PackDownLoadController;
import javafx.scene.layout.BorderPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DownLoadUtils;
import utils.MessageUtils;

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
        Document document;
        try {
            document = Jsoup.connect(projectUrl).get();
        } catch (Exception e) {
            MessageUtils.error("不存在或无法访问", "整合包链接错误");
            return;
        }
        Element element = document.getElementsByClass("listing listing-project-file project-file-listing b-table b-table-a")
                .get(0)
                .getElementsByTag("tbody")
                .get(0).getElementsByTag("tr").get(0)
                .getElementsByAttributeValue("data-action", "modpack-file-link").get(0);

//        if(elementsByClass.size() < 1){
//            MessageUtils.error("出错", "获取整合包文件失败，请确认链接是否正确");
//            return;
//        }else{
//            fileUrl = baseUrl + elementsByClass.get(0).getElementsByTag("a").attr("href");
//            fileUrl = fileUrl.substring(0, fileUrl.length() - 9) + "/file";
//        }
        String downloadUrl ;
        String packName;

        if(element != null){
            downloadUrl = baseUrl + element.attr("href").replace("files", "download") + "/file";
            packName = element.text();
        }else{
            MessageUtils.error("出错", "获取整合包文件失败，请确认链接是否正确");
            return;
        }

        boolean b = false;
        try {
            String fileName = DownLoadUtils.downLoadFile(downloadUrl, null);
            zipFilePath = DownLoadUtils.getRootPath() + "/" + fileName;
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
