package task;

import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import model.DownLoadModel;
import utils.DownLoadUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Cap_Sub
 */
public class DownLoadTask implements Runnable {

    private DownLoadModel downLoadModel;
    private TextArea logText;

    public DownLoadTask(DownLoadModel downLoadModel, TextArea logText) {
        this.downLoadModel = downLoadModel;
        this.logText = logText;
    }

    @Override
    public void run() {
        try {
            String enter = "\n";
            String headStr = encode("下载线程[" + Thread.currentThread().getName() + "]: ");
            logText.appendText(encode(headStr + "开始下载" + downLoadModel.getFileName() + enter));
            Long time = System.currentTimeMillis();
            boolean flag = DownLoadUtils.downLoadFile(downLoadModel.getUrl(), downLoadModel.getFileName(), downLoadModel.getPath(), downLoadModel.getFileSize());
            time = (System.currentTimeMillis() - time) / 1000;
            if(flag){
                logText.appendText(encode(headStr + "下载" + downLoadModel.getFileName() + "完成,用时" + time + "秒" + enter));
            }else{
                logText.appendText(encode(headStr + downLoadModel.getFileName() + "已存在,跳过下载" + enter));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encode(String souStr) throws UnsupportedEncodingException {
        return new String(souStr.getBytes(), "UTF-8");
    }
}
