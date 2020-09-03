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

    public static void downloadForgeNew(String mcVersion, String forgeVersionStr){
        CommonUtils.getPool().submit(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(true){
                if(MessageUtils.check()){
                    try {
                        DownLoadUtils.installForge(new ForgeVersion(mcVersion, null, forgeVersionStr));
                    } catch (IOException e) {
                        MessageUtils.error(e);
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    MessageUtils.error(e);
                }
            }
        });
    }
}
