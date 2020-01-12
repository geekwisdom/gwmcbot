/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.login.LoginDisconnectEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;

public class PacketInLoginDisconnect extends Packet {

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        String errorMessage = readString(in);
        GWmcbot.getInstance().getEventManager().callEvent(new LoginDisconnectEvent(errorMessage));
    }
}
