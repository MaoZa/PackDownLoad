package cn.dawnland.packdownload.netty.packet;

import lombok.Data;

/**
 * @author Cap_Sub
 */
@Data
public abstract class Packet {

    /**
     * 协议版本
     */
    private Byte version = 1;

    private Byte command;

    /**
     * 指令
     */
    public abstract Byte getCommand();

}
