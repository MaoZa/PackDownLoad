package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.configs.RetryIntercepter;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.SECONDS)
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
                File file = new File(saveFilePath + File.separator + filename);
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

    public interface OnDownloadListener{

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file) throws IOException;

        /**
         * 下载进度
         */
        void onDownloading(int progress, String  filename);

        /**
         * 下载异常信息
         */
        void onDownloadFailed(Exception e);
    }

    public String get(final String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

}