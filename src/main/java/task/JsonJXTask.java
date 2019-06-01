package task;

import com.alibaba.fastjson.JSONObject;
import configs.Config;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import utils.DownLoadUtils;
import utils.MessageUtils;
import utils.Upgrader;
import utils.ZipUtils;

import javax.swing.*;
import java.io.*;
import java.net.URLEncoder;
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
    private BorderPane progressPane;

    public JsonJXTask(String zipFilePath, BorderPane progressPane, ExecutorService pool) {
        this.zipFilePath = zipFilePath;
        this.progressPane = progressPane;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            if (jsonPath == null) {
                jsonPath = ZipUtils.getZipEntryFile(zipFilePath, "manifest.json").getPath();
            }
            String fileJson = readJsonData(jsonPath);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");

            try{
                String mcVersion = ((Map)jsonObject.get("minecraft")).get("version") + "";
                String forgeVersion = ((Map)((List)((Map)jsonObject.get("minecraft")).get("modLoaders")).get(0)).get("id") + "";
                String mcjarStrTmp = Config.mcjarStr.replaceFirst("mcVersion", mcVersion).replaceFirst("forgeVersion", forgeVersion);
                File file = new File(DownLoadUtils.getRootPath() + "/核心要求.txt");
                FileOutputStream fos = new FileOutputStream(file);
                PrintStream ps = new PrintStream(fos);
                String[] mcjarStrTmpSplit = mcjarStrTmp.split("\n");
                for (int i = 0; i < mcjarStrTmpSplit.length; i++) {
                    ps.println(mcjarStrTmpSplit[i]);
                }
//                MessageUtils.info(mcjarStrTmp + "\n避免忘记已将要求写入" + file.getPath());
                Runtime.getRuntime().exec("cmd.exe  /c notepad " + file.getPath());
            }catch (Exception e){}

            ZipUtils.unzip(zipFilePath, DownLoadUtils.getRootPath(), progressPane, pool);
            Label label1 = new Label("0/" + files.size());

            ProgressBar progressBar = new ProgressBar();
            Platform.runLater(() -> {
                progressBar.setPrefWidth(230D);
                progressBar.setProgress(0);
                Label label = new Label("下载进度");
                label.setPrefWidth(20D);
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setPrefWidth(50D);
                label.setAlignment(Pos.CENTER_LEFT);
                HBox hb = new HBox();
                hb.setPrefWidth(300D);
                hb.setSpacing(5D);
                hb.setAlignment(Pos.CENTER);
                hb.getChildren().addAll(label, progressBar, label1);
                progressPane.setTop(hb);
                MessageUtils.info("下载中...");
            });

            //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
            //                                                     项目id        文件id

            Iterator<JSONObject> iterator = files.iterator();

            pool.submit(() -> {
                MessageUtils.info("正在下载启动器...");
                try {
                    Upgrader.downLoadFromUrl("https://dawnland.cn/" + URLEncoder.encode("黎明大陆伪正版启动器", "UTF-8") + ".exe", "黎明大陆伪正版启动器.exe" , "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                pool.submit(new FilesDownLoadTask(object, progressBar, label1, files.size()));
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
            MessageUtils.error(e);
            e.printStackTrace();
        }
        //System.out.println("读取文件结束util");
        return strbuffer.toString();
    }
}
