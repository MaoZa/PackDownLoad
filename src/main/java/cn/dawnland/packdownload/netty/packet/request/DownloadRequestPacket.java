package cn.dawnland.packdownload.netty.packet.request;

import cn.dawnland.packdownload.netty.packet.Command;
import cn.dawnland.packdownload.netty.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadRequestPacket extends Packet {

    private String url;

    private String path;

    public DownloadRequestPacket(String url) {
        this.url = url;
    }

    @Override
    public Byte getCommand() {
        return Command.DOWNLOAD_REQUEST;
    }
}
