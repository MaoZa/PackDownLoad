package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.DownLoadUtils;
import com.alibaba.fastjson.JSONObject;
import javafx.concurrent.Task;

/**
 * @author Cap_Sub
 */
public class ModsDownLoadTask extends Task {

    private JSONObject jsonObject;

    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";

    private String baseUrl = "https://www.curseforge.com/minecraft/mc-mods/%s/download/%s/file";

    public ModsDownLoadTask(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    protected Object call() {
        return null;
    }

    @Override
    public void run() {
    }

}
