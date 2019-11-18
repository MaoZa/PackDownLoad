package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.model.ForgeVersion;
import javafx.application.Platform;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Cap_Sub
 * Forge相关工具
 */
public class ForgeUtils {

    private static ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void downloadForge(String mcVersion, String forgeVersion){
        pool.submit(() -> {
            while(true){
                if(MessageUtils.isOk()){
                    String installUrl = new ForgeVersion(mcVersion, forgeVersion).getForgeInstallUrl();
                    try {
                        DownLoadUtils.downloadVersionJson(mcVersion, forgeVersion, installUrl);
                    } catch (IOException e) {
                        MessageUtils.error(e);
                    }
                    MessageUtils.setStatus();
                    if(DownLoadUtils.downloadFaildModS.size() > 0){
//                        CurseUtils.failsMod(DownLoadUtils.downloadFaildModS);
                        Platform.runLater(() -> {
                            LogUtils.error("有" + DownLoadUtils.downloadFaildModS.size() + "个mod下载失败 请尝试重新下载");
                        });
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
