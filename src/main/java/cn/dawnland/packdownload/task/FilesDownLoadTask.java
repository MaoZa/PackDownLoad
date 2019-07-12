package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.LogUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.alibaba.fastjson.JSONObject;
import javafx.concurrent.Task;
import cn.dawnland.packdownload.utils.DownLoadUtils;

/**
 * @author Cap_Sub
 */
public class FilesDownLoadTask extends Task {

    private JSONObject jsonObject;

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    public FilesDownLoadTask(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    protected Object call() {
        return null;
    }

    private int reCount = 0;

    @Override
    public void run() {
        //下载路径格式https://minecraft.curseforge.com/projects/319466/files/2706079/download
        //                                                     项目id        文件id
        String url = "https://www.curseforge.com/minecraft/mc-mods/projectID/download/fileID/file";
        int count = 10;
        for (int i = reCount; i < count; i++) {
            try {
                String enter = "\n";
                url = url.replaceFirst("projectID", jsonObject.get("projectID") + "");
                url = url.replaceFirst("fileID", jsonObject.get("fileID") + "");
                DownLoadUtils.downLoadMod(url, null, MODS_PATH, null);
                break;
            } catch (Exception e) {
                LogUtils.error(e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if((reCount + 1) == count){
                    System.out.println("reCount{}" + reCount);
                    /** 添加下载失败的mod入 **/
                    UIUpdateUtils.modsBarAddOne();
                    DownLoadUtils.downloadFaildModS.putIfAbsent(jsonObject.get("projectID") + "", jsonObject.get("fileID") + "");
                }else{
                    System.out.println(url + "{连接失败正在重试:" + (reCount + 1) + "}");
                }
                reCount++;
            }
        }

    }






}
