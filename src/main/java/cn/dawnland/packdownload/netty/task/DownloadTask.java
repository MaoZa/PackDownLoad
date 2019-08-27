package cn.dawnland.packdownload.netty.task;

import io.netty.channel.ChannelHandlerContext;

public class DownloadTask extends Task<String> {

    public DownloadTask(ChannelHandlerContext ctx, Callback callback) {
        super(ctx, callback);
    }

    @Override
    public String subTask() throws Exception {
        return null;
    }
}
