package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.configs.RetryIntercepter;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Cap_Sub
 * 文件下载工具类（单例模式）
 */
public class OkHttpUtils{

    private static OkHttpUtils downloadUtil;
    private final OkHttpClient okHttpClient;

    public final static ConcurrentMap<String, Object> cells = new ConcurrentHashMap<>();

    public static OkHttpUtils get() {
        if (downloadUtil == null) {
            downloadUtil = new OkHttpUtils();
        }
        return downloadUtil;
    }

    public OkHttpUtils() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new RetryIntercepter(10)).build();
        okHttpClient.dispatcher().setMaxRequests(1000);
        okHttpClient.dispatcher().setMaxRequests(1000);
    }

    /**
     * @param url          下载连接
     * @param saveFilePath 文件储存完整路径+文件名
     * @param listener     下载监听
     */
    public void download(String url, final String saveFilePath, final OnDownloadListener listener) {
        if(!Paths.get(saveFilePath).toFile().exists()){
            try {
                Files.createDirectories(Paths.get(saveFilePath));
            } catch (IOException e) {
                listener.onDownloadFailed(e);
            }
        }
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String filename = response.request().url().uri().getPath();
                filename = filename.substring(filename.lastIndexOf("/") + 1);
                cells.putIfAbsent(filename, call);
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件
                File file = Paths.get(saveFilePath + File.separator + filename).toFile();
                if(file.exists() && file.length() == response.body().contentLength()){
                    listener.onDownloadSuccess(file);
                    return;
                }
                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        MessageUtils.sizeAI.addAndGet(len);
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onDownloading(progress, filename);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);

                }finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }

                }


            }
        });
    }

    public abstract static class OnDownloadListener{

        public OnDownloadListener() {}

        public final Label titleLabel = new Label();
        public final JFXProgressBar modsBar = new JFXProgressBar();
        public final Label barlabel = new Label();
        public final HBox hb = new HBox();

        public boolean flag = false;

        /**
         * 下载完成后的回调方法
         * 如果重写此方法则必须在方法首行执行super.onDownloadSuccess(file)
         * @param file
         */
        public void onDownloadSuccess(File file){
            DownLoadUtils.taskList.getItems().remove(this.hb);
        }

        /**
         * 下载进度
         */
        public void onDownloading(int progress, String  filename){
            if(!flag){
                hb.setPrefWidth(360D);
                hb.setSpacing(10D);
                hb.setAlignment(Pos.CENTER);
                modsBar.setPrefWidth(70D);
                modsBar.setMaxHeight(5D);
                modsBar.setProgress(0);
                titleLabel.setText(filename);
                titleLabel.setPrefWidth(150D);
                titleLabel.setMaxHeight(5);
                barlabel.setAlignment(Pos.CENTER_RIGHT);
                barlabel.setPrefWidth(40D);
                barlabel.setAlignment(Pos.CENTER_LEFT);
                Platform.runLater(() -> {
                    hb.getChildren().addAll(titleLabel, modsBar, barlabel);
                    DownLoadUtils.taskList.getItems().add(hb);
                });
                flag = true;
            }
            Platform.runLater(() -> {
                barlabel.setText(progress + "%");
                modsBar.setProgress(progress / 100D);
            });
        }

        /**
         * 下载异常信息
         */
        public void onDownloadFailed(Exception e){
            System.out.println(e.getMessage());
            MessageUtils.error(e);
        }
    }

    public String get(final String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

}