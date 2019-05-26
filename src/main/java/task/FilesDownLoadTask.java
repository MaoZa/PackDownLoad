package task;

import com.alibaba.fastjson.JSONObject;
import controller.PackDownLoadController;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import main.MainController;
import model.DownLoadModel;
import utils.DownLoadUtils;
import utils.UIUpdateUtils;

/**
 * @author Cap_Sub
 */
public class FilesDownLoadTask extends Task {

    private JSONObject jsonObject;
    private PackDownLoadController packDownLoadController;

    public FilesDownLoadTask(JSONObject jsonObject, PackDownLoadController packDownLoadController) {
        this.jsonObject = jsonObject;
        this.packDownLoadController = packDownLoadController;
    }

    @Override
    protected Object call() {
        return null;
    }

    private int reCount = 0;

    @Override
    public void run() {
        String url = null;
        try {
            String enter = "\n";
            //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
            //                                                     项目id        文件id
            url = "https://minecraft.curseforge.com/projects/projectID/files/fileID/download";
            url = url.replaceFirst("projectID", jsonObject.get("projectID") + "");
            url = url.replaceFirst("fileID", jsonObject.get("fileID") + "");
            boolean flag = DownLoadUtils.downLoadFile(url, null, "mods", null);
            System.out.println(flag ? jsonObject.get("projectID") + "成功" : jsonObject.get("projectID") + "失败");
        } catch (Exception e) {
            if("connect timed out".equals(e.getMessage()) && reCount < 6){
                System.out.println(url + "{连接失败正在重试:" + reCount);
                this.run();
            }else {
                System.out.println(url + ":" + e.getMessage());
            }
        }
    }






}
