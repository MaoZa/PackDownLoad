package utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import task.UnZipSubTask;
import task.UnZipTask;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Cap_Sub
 */
public class ZipUtils {

    public static void unzip(String zipFile, String location, BorderPane progressPane, ExecutorService pool) throws IOException {
        try {

            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }

            ZipFile zf = new ZipFile(zipFile);

            Double proSize = 1D/zf.size();

            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));

            ProgressBar progressBar = new ProgressBar();
            Label label1 = new Label("0/" + zf.size());

            Platform.runLater(() -> {
                progressBar.setPrefWidth(230D);
                progressBar.setProgress(0);
                Label label = new Label("解压进度");
                label.setPrefWidth(20D);
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setPrefWidth(50D);
                label.setAlignment(Pos.CENTER_LEFT);
                HBox hb = new HBox();
                hb.setPrefWidth(300D);
                hb.setSpacing(5D);
                hb.setAlignment(Pos.CENTER);
                hb.getChildren().addAll(label, progressBar, label1);
                progressPane.setCenter(hb);
                Parent parent = progressBar.getParent();
                AnchorPane anchorPane = (AnchorPane) parent.getParent().getParent();
//              borderPane.getChildren().remove(parent);
                MessageUtils.info("正在读取压缩文件，稍等即可");
            });
            pool.submit(new UnZipSubTask(zin, pool, location, progressBar, proSize, label1));
        }
        catch (Exception e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }


    public static List<FileOutputStream> copyFile(String zipFilePath, String ... files){
        System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding")); //防止文件名中有中文时出错

        FileInputStream fis;
        List<FileOutputStream> foss = new ArrayList<>();
        try
        {
            ZipFile zipFile = new ZipFile(zipFilePath);
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = null;
            while((zipEntry = zis.getNextEntry()) != null){
                if(isHave(files, zipEntry.getName())){
                    InputStream is = zipFile.getInputStream(zipEntry);
                    FileOutputStream fos = new FileOutputStream(zipEntry.getName());
                    int len;
                    while((len = is.read()) != -1){
                        fos.write(len);
                    }
                    foss.add(fos);
                }
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        }
        catch (IOException e)
        {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        return foss;
    }

    public static boolean isHave(String[] strs,String s){

        /*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串*/
        int i = strs.length;
        while (i-- > 0){
            if(strs[i] == s || s.startsWith(strs[i])){
                return true;
            }
        }
        return false;
    }

    public static File getZipEntryFile(String zipFilePath, String key) throws Exception {
        FileInputStream fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipFile zipFile = new ZipFile(zipFilePath);
        ZipEntry zipEntry = null;
        while((zipEntry = zis.getNextEntry()) != null){
            if(key.equals(zipEntry.getName())){
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(zipEntry.getName());
                int len;
                while((len = is.read()) != -1){
                    fos.write(len);
                }
                break;
            }
        }
        return new File(zipEntry.getName());
    }


}
