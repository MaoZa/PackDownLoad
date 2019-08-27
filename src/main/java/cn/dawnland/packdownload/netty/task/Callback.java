package cn.dawnland.packdownload.netty.task;

public interface Callback {

    void successCallback();

    void exceptionCallback(Exception e);

}
