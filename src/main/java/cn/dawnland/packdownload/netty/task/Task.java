package cn.dawnland.packdownload.netty.task;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public abstract class Task<T> implements Runnable {

    private String msg;
    private Integer type;
    private Callback callback;

    private ChannelHandlerContext ctx;

    public Task(ChannelHandlerContext ctx, Callback callback) {
        this.ctx = ctx;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            Object returnObj = subTask();
            ctx.channel().writeAndFlush(returnObj);
        } catch (Exception e) {
            callback.exceptionCallback(e);
        }
        callback.successCallback();
    }

    public abstract T subTask() throws Exception;
}
