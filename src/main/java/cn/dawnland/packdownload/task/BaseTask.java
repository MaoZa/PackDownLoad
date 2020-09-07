package cn.dawnland.packdownload.task;

import cn.dawnland.packdownload.utils.CommonUtils;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseTask<T> extends Task<T> {

    protected Callback<T> callback;
    protected int progress = 0;
    protected final JFXListView taskList = CommonUtils.getTaskList();
    protected final JFXProgressBar progressBar = new JFXProgressBar();
    protected final Label messageLabel = new Label();

    @Override
    protected T call() { return null; }

    @Override
    public void run() {
        try {
            subTask();
        } catch (Exception e) {
            callback.exceptionCallback(e);
        }
    }

    /**
     * @return String filename
     * @throws Exception
     */
    abstract void subTask() throws Exception;

}
