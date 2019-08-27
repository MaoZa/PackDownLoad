package cn.dawnland.packdownload.task;

import javafx.concurrent.Task;

public abstract class BaseTask<T> extends Task<T> {

    protected Callback<T> callback;

    public BaseTask(Callback<T> callback) {
        this.callback = callback;
    }

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
