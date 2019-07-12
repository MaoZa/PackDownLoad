package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.model.ForgeVersion;
import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.dawnland.packdownload.utils.DownLoadUtils.isOpenLanauch;

public class ForgeUtils {

    private static ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void downloadForge(String mcVersion, String forgeVersion){
        pool.submit(() -> {
            while(true){
                System.out.println(MessageUtils.status);
                if(MessageUtils.isOk()){
                    String installUrl = new ForgeVersion(mcVersion, forgeVersion).getForgeInstallUrl();
                    try {
                        DownLoadUtils.downloadVersionJson(mcVersion, forgeVersion, installUrl);
                    } catch (IOException e) {
                        MessageUtils.error(e);
                    }
                    MessageUtils.setStatus();
                    if(DownLoadUtils.downloadFaildModS.size() > 0){
                        CurseUtils.failsMod(DownLoadUtils.downloadFaildModS);
                    }
                    isOpenLanauch(MessageUtils.resultLabel);
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

    public static JSONArray getLibs(File jarFile){
        return null;
    }

}
