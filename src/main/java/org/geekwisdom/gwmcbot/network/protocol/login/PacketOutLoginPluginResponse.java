package org.geekwisdom.gwmcbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutLoginPluginResponse extends Packet {

    private int msgId;
    private boolean hasResponse;
    private byte[] data;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeVarInt(msgId, out);
        out.writeBoolean(hasResponse);
        if (hasResponse)
            out.write(data);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        // This packet is outgoing only
    }
}
