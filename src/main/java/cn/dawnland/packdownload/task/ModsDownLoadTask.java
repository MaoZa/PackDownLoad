package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.netty.config.NettyConfig;
import cn.dawnland.packdownload.netty.packet.request.DownloadRequestPacket;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.LogUtils;
import cn.dawnland.packdownload.utils.OkHttpUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;

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
