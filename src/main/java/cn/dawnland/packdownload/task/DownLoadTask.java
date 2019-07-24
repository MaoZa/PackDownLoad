package cn.dawnland.packdownload.task;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import cn.dawnland.packdownload.launcher.MainController;
import cn.dawnland.packdownload.model.DownLoadModel;
import cn.dawnland.packdownload.utils.DownLoadUtils;
import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.UIUpdateUtils;

/**
 * @author Cap_Sub
 */
public class DownLoadTask extends Task {

    private DownLoadModel downLoadModel;
    private MainController mainController;

    public DownLoadTask(DownLoadModel downLoadModel, MainController mainController) {
        this.downLoadModel = downLoadModel;
        this.mainController = mainController;
    }

    @Override
    protected Object call() {
        return null;
    }

    @Override
    public void run() {
        try {
            TextArea logText = mainController.getLogText();
            String enter = "\n";
            String headStr = "下载线程[" + Thread.currentThread().getName() + "]: ";
            String filename = downLoadModel.getFileName();
            headStr = headStr.replaceFirst("pool", "线程组").replaceFirst("thread", "子线程");
            UIUpdateUtils.textAreaAppend(logText, headStr + "开始下载" + filename + enter);
            Long time = System.currentTimeMillis();
            boolean flag = DownLoadUtils.downLoadFile(downLoadModel.getUrl(), filename, downLoadModel.getPath(), downLoadModel.getFileSize());
            time = (System.currentTimeMillis() - time) / 1000;
            if(flag){
                UIUpdateUtils.textAreaAppend(logText, headStr + "下载" + filename + "完成,用时" + time + "秒" + enter);
            }else {
                UIUpdateUtils.textAreaAppend(logText, headStr + filename + "已存在,跳过下载" + enter);
            }

        } catch (Exception e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }






}