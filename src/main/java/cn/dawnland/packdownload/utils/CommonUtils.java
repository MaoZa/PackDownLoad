package cn.dawnland.packdownload.utils;

public class CommonUtils {

    /**
     * 退出软件
     */
    public static void appExit(){
        Upgrader.deleteTmpFile();
        System.exit(0);
    }

}
