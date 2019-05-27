package controller;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import task.DownLoadTask;
import task.FilesDownLoadTask;
import utils.DownLoadUtils;
import utils.ZipUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;

public class PackDownLoadController {

    @FXML private BorderPane progressPane;
    @FXML private Label resultLabel;

    private String zipFilePath = "D:\\PackDownLoad\\src\\main\\resources\\SkyFactory4-4.0.5.zip";
    private String jsonPath;

    public void startPackDownLoad(){
        try {
            if (jsonPath == null) {
                jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").getPath();
            }
            String fileJson = readJsonData(jsonPath);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getRootPath(), progressPane);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(230D);
            progressBar.setProgress(0);
            Label label = new Label("下载进度");
            label.setPrefWidth(20D);
            label.setAlignment(Pos.CENTER_RIGHT);
            Label label1 = new Label("0/" + files.size());
            label.setPrefWidth(50D);
            label.setAlignment(Pos.CENTER_LEFT);
            HBox hb = new HBox();
            hb.setPrefWidth(300D);
            hb.setSpacing(5D);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(label, progressBar, label1);
            progressPane.setCenter(hb);

            Platform.runLater(() -> {
                AnchorPane anchorPane = (AnchorPane) progressBar.getParent().getParent().getParent();
                HBox hBox = (HBox) anchorPane.getChildren().get(1);
                Label resultLabel = (Label)hBox.getChildren().get(0);
                resultLabel.setText("下载中...");
            });

            //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
            //                                                     项目id        文件id

            ExecutorService pool = Executors.newFixedThreadPool(50);
            Iterator<JSONObject> iterator = files.iterator();
            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                pool.execute(new FilesDownLoadTask(object, this, progressBar, label1, files.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取json文件并且转换成字符串
     * @param pactFile 文件的路径
     * @return
     * @throws IOException
     */
    public static String readJsonData(String pactFile) throws IOException {
        // 读取文件数据
        //System.out.println("读取文件数据util");

        StringBuffer strbuffer = new StringBuffer();
        File myFile = new File(pactFile);//"D:"+File.separatorChar+"DStores.json"
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in  = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);  //new String(str,"UTF-8")
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        //System.out.println("读取文件结束util");
        return strbuffer.toString();
    }

}
