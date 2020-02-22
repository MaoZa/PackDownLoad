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
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        PrintStream ps = new PrintStream(fos);
        e.printStackTrace(ps);
    }

    public static void error(String msg){
        File file = new File(DownLoadUtils.getRootPath() + "/error.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fos);
        ps.append(LocalDateTime.now() + ": " + msg);
    }

    public static void info(String msg) {
        File file = new File(DownLoadUtils.getRootPath() + "/info.txt");
        PrintStream ps = null;
        try {
            ps = new PrintStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ps.append(LocalDateTime.now() + ": " + msg);
        System.out.println(LocalDateTime.now() + ": " + msg);
    }

    public static void addSuccessMod(CurseModInfo curseModInfo) {
        File file = new File(DownLoadUtils.getRootPath() + "/successMod.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(curseModInfo.getDisplayName() + "\n");
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
