/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/15
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.play.UpdatePlayerListEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketInPlayerListItem extends Packet {

    @Getter private static Set<UUID> currPlayers = new HashSet<>();

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        //Only incoming packet
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        int action = readVarInt(in);
        int playerCount = readVarInt(in);
        for(int i = 0; i < playerCount; i++) {
            UUID uuid = readUUID(in);
            if (action == 0)        //ADD
                currPlayers.add(uuid);
            else if (action == 4)   //REMOVE
                currPlayers.remove(uuid);
        }
        in.skipBytes(in.getAvailable());

        GWmcbot.getInstance().getEventManager().callEvent(new UpdatePlayerListEvent(getCurrPlayers()));
    }
}
