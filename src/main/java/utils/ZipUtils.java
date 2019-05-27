package utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import task.UnZipTask;

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

    public static void unzip(String zipFile, String location, BorderPane progressPane) throws IOException {
        try {

            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }

            ZipFile zf = new ZipFile(zipFile);

            Double proSize = 1D/zf.size();

            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));

            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(230D);
            progressBar.setProgress(0);
            Label label = new Label("解压进度");
            label.setPrefWidth(20D);
            label.setAlignment(Pos.CENTER_RIGHT);
            Label label1 = new Label("0/" + zf.size());
            label.setPrefWidth(50D);
            label.setAlignment(Pos.CENTER_LEFT);
            HBox hb = new HBox();
            hb.setPrefWidth(300D);
            hb.setSpacing(5D);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(label, progressBar, label1);
            progressPane.setBottom(hb);

            ExecutorService pool = Executors.newFixedThreadPool(50);

            Platform.runLater(() -> {
                Parent parent = progressBar.getParent();
                AnchorPane anchorPane = (AnchorPane) parent.getParent().getParent();
                BorderPane borderPane = (BorderPane) parent.getParent();
//                            borderPane.getChildren().remove(parent);
                HBox hBox = (HBox) anchorPane.getChildren().get(1);
                Label resultLabel = (Label)hBox.getChildren().get(0);
                resultLabel.setText("正在读取压缩文件，稍等即可");
            });

            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                // TODO: 2019/5/27 多线程读流优化 目前没有解决方案 主线程读流会导致舞台未响应
                List<Integer> cs = new ArrayList<>();
                try {
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        cs.add(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pool.submit(new UnZipTask(location, ze, progressBar, cs, proSize, label1));
            }
            zin.close();
            zf.close();
        }
        catch (Exception e) {
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


}
