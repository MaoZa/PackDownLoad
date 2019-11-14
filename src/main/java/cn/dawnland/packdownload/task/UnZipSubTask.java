package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.MessageUtils;

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

    public UnZipSubTask(ZipInputStream zin, ExecutorService pool, String location) {
        this.zin = zin;
        this.pool = pool;
        this.location = location;
    }

    @Override
    public void run() {
        try {
            while ((ze = zin.getNextEntry()) != null) {
                List<Integer> cs = new ArrayList<>();
                try {
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        cs.add(c);
                    }
                } catch (IOException e) {
                    MessageUtils.error(e);
                    e.printStackTrace();
                }
                pool.submit(new UnZipTask(location, ze, cs));
            }
        }catch (Exception e){
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }
}
