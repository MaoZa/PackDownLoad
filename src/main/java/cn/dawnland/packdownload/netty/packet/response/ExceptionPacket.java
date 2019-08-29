package cn.dawnland.packdownload.netty.packet.response;

import cn.dawnland.packdownload.netty.packet.Command;
import cn.dawnland.packdownload.netty.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionPacket extends Packet {

    private Byte code;
    private String msg;
    private LocalDateTime timestamp;

    public ExceptionPacket() {
        this.code = -1;
        this.msg = "未知异常";
        this.timestamp = LocalDateTime.now();
    }

    public ExceptionPacket(String msg) {
        this.code = -1;
        this.msg = msg;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public Byte getCommand() {
        return Command.EXCEPTION_RESPONSE;
    }
}
