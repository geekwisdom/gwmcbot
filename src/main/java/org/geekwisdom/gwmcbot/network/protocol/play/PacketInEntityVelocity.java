/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.play.EntityVelocityEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInEntityVelocity extends Packet {

    @Getter private short x;
    @Getter private short y;
    @Getter private short z;
    @Getter private int eid;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        eid = readVarInt(in);
        x = in.readShort();
        y = in.readShort();
        z = in.readShort();

        GWmcbot.getInstance().getEventManager().callEvent(new EntityVelocityEvent(x, y, z, eid));
    }
}
