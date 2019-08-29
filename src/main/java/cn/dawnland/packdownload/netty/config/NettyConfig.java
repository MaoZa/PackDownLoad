package cn.dawnland.packdownload.netty.config;

import cn.dawnland.packdownload.netty.coder.PacketDecoder;
import cn.dawnland.packdownload.netty.coder.PacketEncoder;
import cn.dawnland.packdownload.netty.handler.ClientHandler;
import cn.dawnland.packdownload.utils.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyConfig {

    private int workerCount = 100;
    private int tcpPort = 8009;
    private String host = "localhost";
    private EventLoopGroup workerGroup = new NioEventLoopGroup(workerCount);

    public Bootstrap bootstrap() {
        Bootstrap b = null;
        try{
            b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("decoder", new PacketDecoder());
                            pipeline.addLast("handler", new ClientHandler());
                            pipeline.addLast("encoder", new PacketEncoder());
                        }
                    });
            b.connect(host, tcpPort);
        }catch (Exception e){
            MessageUtils.info("服务器连接失败, 当前只可使用普通加速下载");
        }
        return b;
    }

}