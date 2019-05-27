package task;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZipSubTask implements Runnable {

    private ZipEntry ze;
    private ZipInputStream zin;
    private ExecutorService pool;
    private String location;
    private ProgressBar progressBar;
    private Double proSize;
    private Label label;

    public UnZipSubTask(ZipInputStream zin, ExecutorService pool, String location, ProgressBar progressBar, Double proSize, Label label) {
        this.zin = zin;
        this.pool = pool;
        this.location = location;
        this.progressBar = progressBar;
        this.proSize = proSize;
        this.label = label;
    }

    @Override
    public void run() {
        try {
            while ((ze = zin.getNextEntry()) != null) {
                // TODO: 2019/5/27 多线程读流优化 目前没有解决方案 主线程读流会导致舞台未响应
                // TODO: 2019/5/27 可尝试写一个子线程Task实现一个线程专门处理读流 避免在主线程中进行读流

                List<Integer> cs = new ArrayList<>();
                try {
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        cs.add(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pool.submit(new UnZipTask(location, ze, progressBar, cs, proSize, label));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
