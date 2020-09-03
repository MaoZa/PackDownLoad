package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.model.manifest.Manifest;
import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;

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
    private List<Integer> cs;
    private Manifest manifest;

    private FileOutputStream fos;

    public UnZipTask(Manifest manifest, String location, ZipEntry ze, List<Integer> cs) {
        this.manifest = manifest;
        this.location = location;
        this.ze = ze;
        this.cs = cs;
    }

    @Override
    public void run() {
        try {
            String path = location + "\\" + ze.getName();
            if(location.indexOf("versions") > 0){
                path = path.replaceFirst(manifest.getOverrides(), "");
            }else {
                if(path.indexOf(".minecraft") > 0){
                    path = path.replaceFirst(manifest.getOverrides() + "/", "");
                }
                path = path.replaceFirst(manifest.getOverrides(), ".minecraft");
            }
            File unzipFile = new File(path);
            if (ze.isDirectory()) {
                unzipFile.mkdirs();
            }else {
                unzipFile.getParentFile().mkdirs();
                fos = new FileOutputStream(unzipFile.getPath());
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
