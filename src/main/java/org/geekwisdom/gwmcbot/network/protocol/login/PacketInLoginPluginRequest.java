package org.geekwisdom.gwmcbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.login.LoginPluginRequestEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInLoginPluginRequest extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int msgId = readVarInt(in);
        String channel = readString(in);
        byte[] data = new byte[in.getAvailable()];
        in.readFully(data);
        GWmcbot.getInstance().getEventManager().callEvent(new LoginPluginRequestEvent(msgId, channel, data));
    }
}
