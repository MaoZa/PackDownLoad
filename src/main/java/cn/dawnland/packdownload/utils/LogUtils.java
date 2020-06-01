package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.model.curse.CurseModInfo;

import java.io.*;
import java.time.LocalDateTime;

/**
 * @author Cap_Sub
 * 日志工具
 */
public class LogUtils {

    public static void error(Exception e){
        File file = new File(DownLoadUtils.getRootPath() + "/error.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(e.getLocalizedMessage() + "\n");
            fileWriter.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void error(String msg){
        File file = new File(DownLoadUtils.getRootPath() + "/error.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(msg + "\n");
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void info(String msg) {
        File file = new File(DownLoadUtils.getRootPath() + "/info.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(msg + "\n");
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(LocalDateTime.now() + ": " + msg);
    }

    public static synchronized void addSuccessMod(CurseModInfo curseModInfo) {
        File file = new File(DownLoadUtils.getPackPath() + "/successMod.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(curseModInfo.getDisplayName() + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileWriter != null){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
