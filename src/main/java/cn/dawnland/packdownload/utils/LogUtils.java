package cn.dawnland.packdownload.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

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

}
