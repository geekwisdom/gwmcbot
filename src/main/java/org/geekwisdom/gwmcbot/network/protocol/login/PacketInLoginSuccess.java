/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.login.LoginSuccessEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInLoginSuccess extends Packet {

    @Getter private UUID uuid;
    @Getter private String userName;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        String uuidStr = readString(in).replace("-","");
        this.uuid = new UUID(new BigInteger(uuidStr.substring(0, 16), 16).longValue(), new BigInteger(uuidStr.substring(16), 16).longValue());
        this.userName = readString(in);

        GWmcbot.getInstance().getEventManager().callEvent(new LoginSuccessEvent(uuid, userName));
    }
}
