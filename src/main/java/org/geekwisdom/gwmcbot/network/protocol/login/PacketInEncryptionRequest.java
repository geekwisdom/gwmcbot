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
import org.geekwisdom.gwmcbot.event.login.EncryptionRequestEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;
import org.geekwisdom.gwmcbot.network.utils.CryptManager;

import java.io.IOException;
import java.security.PublicKey;

@AllArgsConstructor
@NoArgsConstructor
public class PacketInEncryptionRequest extends Packet {

    @Getter private String serverId;
    @Getter private PublicKey publicKey;
    @Getter private byte[] verifyToken;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException {
        this.serverId = readString(in);
        this.publicKey = CryptManager.decodePublicKey(readBytesFromStream(in));
        this.verifyToken = readBytesFromStream(in);

        GWmcbot.getInstance().getEventManager().callEvent(new EncryptionRequestEvent(serverId, publicKey, verifyToken));
    }
}
