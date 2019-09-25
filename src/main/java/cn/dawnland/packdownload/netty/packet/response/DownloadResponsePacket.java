package cn.dawnland.packdownload.netty.packet.response;

import cn.dawnland.packdownload.netty.packet.Command;
import cn.dawnland.packdownload.netty.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResponsePacket extends Packet {

    private String url;

    private String path;

    @Override
    public Byte getCommand() {
        return Command.DOWNLOAD_RESPONSE;
    }
}
