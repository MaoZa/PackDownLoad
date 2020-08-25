package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.model.manifest.Manifest;
import cn.dawnland.packdownload.utils.LogUtils;
import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Cap_Sub
 */
public class UnZipSubTask implements Runnable {

    private ZipEntry ze;
    private ZipInputStream zin;
    private ExecutorService pool;
    private String location;
    private Manifest manifest;

    public UnZipSubTask(Manifest manifest, ZipInputStream zin, ExecutorService pool, String location) {
        this.manifest = manifest;
        this.zin = zin;
        this.pool = pool;
        this.location = location;
    }

    @Override
    public void run() {
        try {
            while ((ze = zin.getNextEntry()) != null) {
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
                if(!ze.isDirectory() && unzipFile != null && unzipFile.exists() && unzipFile.length() == ze.getSize()){
                    LogUtils.info(ze.getName() + "跳过-已解压");
                    UIUpdateUtils.unzipBarAddOne();
                    continue;
                }
                List<Integer> cs = new ArrayList<>();
                try {
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        cs.add(c);
                    }
                } catch (IOException e) {
                    MessageUtils.error(e);
                    e.printStackTrace();
                }
                pool.submit(new UnZipTask(manifest, location, ze, cs));
            }
        }catch (Exception e){
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }
}
