package cn.dawnland.packdownload.utils;

import java.io.*;
import java.util.Date;

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
        ps.append(new Date() + ": " + msg);
    }

    public static void info(String msg){
        File file = new File(DownLoadUtils.getRootPath() + "/info.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fos);
        ps.append(new Date() + ": " + msg);
    }

}
