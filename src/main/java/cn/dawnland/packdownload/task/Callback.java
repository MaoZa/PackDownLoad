package cn.dawnland.packdownload.task;

public interface Callback<T> {

    String messageCallback(String message);

    //%
    String progressCallback(int progress, Object temp);

    String successCallback(T result);

    String exceptionCallback(Exception e);

}
