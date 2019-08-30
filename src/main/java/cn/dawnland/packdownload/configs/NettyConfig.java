package cn.dawnland.packdownload.configs;

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
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("handler", new ClientHandler());
                            pipeline.addLast("encoder", new StringEncoder());
                        }
                    });
            b.connect(host, tcpPort);
        }catch (Exception e){
            MessageUtils.info("服务器连接失败, 当前无法使用加速下载");
        }
        return b;
    }

}