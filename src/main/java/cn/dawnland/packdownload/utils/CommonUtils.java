package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.task.JsonJXTask;

import java.util.Objects;

/**
 * @author Cap_Sub
 * 公共工具
 */
public class CommonUtils {

    /**
     * 退出软件
     */
    public static void appExit(){
//        FileUtils.deleteTmpFile();
        if(Objects.nonNull(JsonJXTask.manifest)){
            JsonJXTask.manifest.save();
        }
        System.exit(0);
    }

}
