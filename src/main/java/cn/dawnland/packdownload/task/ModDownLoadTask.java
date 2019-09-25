package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.OkHttpUtils;

import java.io.File;

public class ModDownLoadTask extends BaseTask<String> {

    private final String url;
    private final String path;

    public ModDownLoadTask(Callback<String> callback, String url, String path) {
        super(callback);
        this.url = url;
        this.path = path;
    }

    @Override
    void subTask() {
        DownLoadUtils.downLoadMod(url, path, new OkHttpUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                callback.successCallback(file.getName());
            }
            @Override
            public void onDownloading(int progress, String filename) {
                callback.progressCallback(progress, filename);
            }
            @Override
            public void onDownloadFailed(Exception e) {
                callback.exceptionCallback(e);
            }
        });
    }
}
