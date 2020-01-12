/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package org.geekwisdom.gwmcbot.network.protocol.handshake;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutHandshake extends Packet {

    private String serverName;
    private int serverPort;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeVarInt(GWmcbot.getInstance().getServerProtocol(), out);
        writeString(serverName, out);
        out.writeShort(serverPort);
        writeVarInt(2, out); //next State = 2 -> LOGIN
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}
