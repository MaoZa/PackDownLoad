package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.model.CurseModInfo;
import cn.dawnland.packdownload.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Cap_Sub
 */
public class JsonJXTask implements Runnable {

    private ExecutorService pool;

    private String jsonPath;
    private String zipFilePath;
    private JFXListView taskList;

    public JsonJXTask(String zipFilePath, JFXListView taskList, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.taskList = taskList;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            if (jsonPath == null) {
                jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").getPath();
            }
            MessageUtils.downloadSpeedStart();
            String fileJson = FileUtils.readJsonData(jsonPath);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");
            String mcVersion = ((Map)jsonObject.get("minecraft")).get("version") + "";
            String forgeVersion = ((Map)((List)((Map)jsonObject.get("minecraft")).get("modLoaders")).get(0)).get("id") + "";
            ForgeUtils.downloadForge(mcVersion, forgeVersion);

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getPackPath(), taskList, pool);
            Iterator<JSONObject> iterator = files.iterator();
            pool.submit(() -> {
                MessageUtils.info("正在下载启动器...");
                try {
                    Upgrader.downLoadFromUrl("https://dawnland.cn/" + URLEncoder.encode("黎明大陆伪正版启动器", "UTF-8") + ".exe", "黎明大陆伪正版启动器.exe" , "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            Platform.runLater(() -> {
                JFXProgressBar modsBar = new JFXProgressBar();
                Label modsLabel = new Label("下载进度");
                modsBar.setPrefWidth(70);
                HBox hb = new HBox();
                Label label = new Label();
                hb.setPrefWidth(350D);
                hb.setSpacing(10D);
                hb.setAlignment(Pos.CENTER);
                modsBar.setPrefWidth(130D);
                modsBar.setMaxHeight(5D);
                modsBar.setProgress(0);
                modsLabel.setPrefWidth(60D);
                modsLabel.setMaxHeight(5);
                modsLabel.setAlignment(Pos.CENTER_LEFT);
                label.setPrefWidth(60D);
                label.setAlignment(Pos.CENTER_RIGHT);
                MessageUtils.info("正在下载所需文件，请耐心等待");
                Platform.runLater(() -> {
                    hb.getChildren().addAll(modsLabel, modsBar, label);
                    DownLoadUtils.taskList.getItems().add(hb);
                });
                pool.submit(()-> UIUpdateUtils.initMods(hb, modsBar, label, files.size()));
            });
            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                request(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    private String baseUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/%s/file/%s";

    public void request(JSONObject jsonObject) throws IOException {
        String projectId = jsonObject.get("projectID").toString();
        String fileId = jsonObject.get("fileID").toString();
        String url = String.format(baseUrl, projectId, fileId);
        CurseModInfo curseModInfo = JSONObject.parseObject(doGet(new URL(url)), CurseModInfo.class);
        Path path = Paths.get(MODS_PATH + File.separator + curseModInfo.getDisplayName());
        if(Files.exists(path)){
            File file = new File(path.toString());
            if(file.length() == curseModInfo.getFileLength()){
                UIUpdateUtils.modsBarAddOne();
                LogUtils.info(file.getName() + "已下载{跳过}");
                return;
            }
        }
        pool.submit(new ModDownLoadTask(new Callback<String>() {
            @Override
            public String progressCallback(int progress, Object temp) {
                return null;
            }

            @Override
            public String successCallback(String result) {
                return null;
            }

            @Override
            public String exceptionCallback(Exception e) {
                return null;
            }
        }, curseModInfo, MODS_PATH));
    }

    private String doGet(URL url) throws IOException {
        return readFullyAsString(createConnection(url).getInputStream());
    }
    private String readFullyAsString(InputStream stream) throws IOException {
        return readFully(stream).toString();
    }
    private HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        return connection;
    }
    private ByteArrayOutputStream readFully(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            copyTo(is, result);
            return result;
        }
    }
    private final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private void copyTo(InputStream src, OutputStream dest) throws IOException {
        copyTo(src, dest, new byte[DEFAULT_BUFFER_SIZE]);
    }
    private static void copyTo(InputStream src, OutputStream dest, byte[] buf) throws IOException {
        while (true) {
            int len = src.read(buf);
            if (len == -1){ break; }
            dest.write(buf, 0, len);
        }
    }
}
