package cn.dawnland.packdownload.netty.packet;

/**
 * @author Cap_Sub
 */
public interface Command {

    Byte DOWNLOAD_REQUEST = 1;
    Byte DOWNLOAD_RESPONSE = 2;

    Byte EXCEPTION_RESPONSE = -1;

    Packet getCommand(Byte command);
}
