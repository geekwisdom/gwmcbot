/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/19
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

@AllArgsConstructor
public class PacketOutUseEntity extends Packet {

    @Getter private int eId;
    @Getter private int action;
    @Getter private float x;
    @Getter private float y;
    @Getter private float z;
    @Getter private int hand;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        writeVarInt(eId, out);
        writeVarInt(action, out);
        if(action == 2) {
            out.writeFloat(x);
            out.writeFloat(y);
            out.writeFloat(z);
        }
        if(action == 0 || action == 2) {
            writeVarInt(hand, out);
        }
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {

    }
}
