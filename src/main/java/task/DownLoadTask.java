package task;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import model.DownLoadModel;
import sample.MainController;
import utils.DownLoadUtils;
import utils.UIUpdateUtils;

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
            headStr = headStr.replaceFirst("pool", "线程组").replaceFirst("thread", "子线程");
            UIUpdateUtils.textAreaAppend(logText, headStr + "开始下载" + downLoadModel.getFileName() + enter);
            Long time = System.currentTimeMillis();
            boolean flag = DownLoadUtils.downLoadFile(downLoadModel.getUrl(), downLoadModel.getFileName(), downLoadModel.getPath(), downLoadModel.getFileSize());
            time = (System.currentTimeMillis() - time) / 1000;
            if(flag){
                UIUpdateUtils.textAreaAppend(logText, headStr + "下载" + downLoadModel.getFileName() + "完成,用时" + time + "秒" + enter);
            }else {
                UIUpdateUtils.textAreaAppend(logText, headStr + downLoadModel.getFileName() + "已存在,跳过下载" + enter);
            }
            UIUpdateUtils.updateProgress(mainController.getProgressBar(), mainController.getDownLoadModels().size());
            double percentage = 100D/ mainController.getDownLoadModels().size();
            percentage += "".equals(mainController.getPercentage().getText()) ? 0 : Double.valueOf(mainController.getPercentage().getText().replaceFirst("%", ""));
            UIUpdateUtils.updateLable(mainController.getPercentage(), percentage + "%");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
