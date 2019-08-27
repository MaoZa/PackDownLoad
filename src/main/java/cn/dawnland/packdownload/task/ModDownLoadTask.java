package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.OkHttpUtils;

import java.io.File;

public class ModDownLoadTask extends BaseTask<String> {

    private String projectId;
    private String fileId;
    private final String MODS_PATH = DownLoadUtils.getPackPath() + "/mods";
    private final String BASE_DOWNLOAD_URL = "https://www.curseforge.com/minecraft/mc-mods/%s/download/%s/file";

    public ModDownLoadTask(Callback<String> callback, String projectId, String fileId) {
        super(callback);
        this.projectId = projectId;
        this.fileId = fileId;
    }

    @Override
    void subTask() throws Exception {
        String url = String.format(BASE_DOWNLOAD_URL, projectId, fileId);
        DownLoadUtils.downLoadMod(url, MODS_PATH, new OkHttpUtils.OnDownloadListener() {
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
