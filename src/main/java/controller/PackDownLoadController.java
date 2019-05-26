package controller;

import com.alibaba.fastjson.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import task.DownLoadTask;
import task.FilesDownLoadTask;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackDownLoadController {

    @FXML private Pane progressPane;

    private String jsonPath = "G:/SkyFactory4-4.0.5/manifest.json";

    public void startPackDownLoad(){
        try {
            String fileJson = readJsonData(jsonPath);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            System.out.println(jsonObject);

            //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
            //                                                     项目id        文件id

            ExecutorService pool = Executors.newFixedThreadPool(5);
            List<JSONObject> files = (List<JSONObject>) jsonObject.get("files");
            Iterator<JSONObject> iterator = files.iterator();
            while (iterator.hasNext()){
                JSONObject object = iterator.next();
                pool.execute(new FilesDownLoadTask(object, this));
            }
        } catch (IOException e) {
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
