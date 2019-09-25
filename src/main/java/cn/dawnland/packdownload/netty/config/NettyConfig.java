package cn.dawnland.packdownload.netty.config;

import cn.dawnland.packdownload.netty.coder.PacketDecoder;
import cn.dawnland.packdownload.netty.coder.PacketEncoder;
import cn.dawnland.packdownload.netty.coder.Spliter;
import cn.dawnland.packdownload.netty.handler.ModDownloadHandler;
import cn.dawnland.packdownload.netty.packet.Packet;
import cn.dawnland.packdownload.utils.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyConfig {

    private int workerCount = 100;
    private int tcpPort = 8009;
    public static String host = "pds.dawnland.cn";
//    public static String host = "pds.dawnland.cn";
    private EventLoopGroup workerGroup = new NioEventLoopGroup(workerCount);

    private static Channel channel;

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
                            pipeline.addLast("spliter", new Spliter());
                            pipeline.addLast("decoder", new PacketDecoder());
                            pipeline.addLast("handler", new ModDownloadHandler());
                            pipeline.addLast("encoder", new PacketEncoder());
                        }
                    });
            this.channel = b.connect(host, tcpPort).channel();
        }catch (Exception e){
            MessageUtils.info("服务器连接失败, 当前只可使用普通加速下载");
        }
        return b;
    }

    public static void request(Packet packet){
        channel.writeAndFlush(packet);

    }

    public static void main(String[] args) {
        new NettyConfig().bootstrap();
    }
}