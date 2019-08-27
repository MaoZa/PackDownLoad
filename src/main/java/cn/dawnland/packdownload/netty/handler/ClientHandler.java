package cn.dawnland.packdownload.netty.handler;

import cn.dawnland.packdownload.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.channel().writeAndFlush("我连上来拉!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        MessageUtils.info("服务器连接成功, 当前可使用加速下载");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        MessageUtils.info("服务器连接断开, 当前无法使用加速下载");
        super.channelUnregistered(ctx);
    }
}
