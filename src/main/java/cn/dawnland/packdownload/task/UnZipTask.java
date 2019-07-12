package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.UIUpdateUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.MessageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * @author Cap_Sub
 */
public class UnZipTask implements Runnable{

    private String location;
    private ZipEntry ze;
    private ProgressBar progressBar;
    private List<Integer> cs;
    private Double proSize;
    private Label proLabel;

    private FileOutputStream fos;

    public UnZipTask(String location, ZipEntry ze, List<Integer> cs) {
        this.location = location;
        this.ze = ze;
        this.cs = cs;
    }

    @Override
    public void run() {
        try {
            String path = location + "\\" + ze.getName();
            if(location.indexOf("versions") > 0){
                path = path.replaceFirst("overrides", "");
            }else {
                if(path.indexOf(".minecraft") > 0){
                    path = path.replaceFirst("overrides/", "");
                }
                path = path.replaceFirst("overrides", ".minecraft");
            }
            File unzipFile = new File(path);
            if (ze.isDirectory()) {
                unzipFile.mkdirs();
            }else {
                unzipFile.getParentFile().mkdirs();
                fos = new FileOutputStream(unzipFile.getPath(), false);
                cs.forEach(c -> {
                    try {
                        fos.write(c);
                    } catch (IOException e) {
                        MessageUtils.error(e);
                        e.printStackTrace();
                    }
                });
            }
        }catch (Exception e){
            MessageUtils.error(e);
        }finally {
            UIUpdateUtils.unzipBarAddOne();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    MessageUtils.error(e);
                    e.printStackTrace();
                }
            }
        }
    }
}
