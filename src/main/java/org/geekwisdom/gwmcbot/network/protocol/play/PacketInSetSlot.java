/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.play.UpdateSlotEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSetSlot extends Packet {

    @Getter private int windowId;
    @Getter private short slotId;
    @Getter private ByteArrayDataOutput slotData;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        this.windowId = in.readByte();
        this.slotId = in.readShort();

        byte[] bytes = new byte[in.getAvailable()];
        in.readBytes(bytes);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(bytes.clone());
        this.slotData = out;

        GWmcbot.getInstance().getEventManager().callEvent(new UpdateSlotEvent(windowId, slotId, slotData));
    }
}
