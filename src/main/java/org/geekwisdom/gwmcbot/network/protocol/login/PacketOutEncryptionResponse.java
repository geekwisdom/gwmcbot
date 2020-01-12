/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.login;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;
import org.geekwisdom.gwmcbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.PublicKey;

@NoArgsConstructor
@AllArgsConstructor
public class PacketOutEncryptionResponse extends Packet {

    @Getter private String serverId;
    @Getter private PublicKey publicKey;
    @Getter private byte[] verifyToken;
    @Getter private SecretKey secretKey;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) throws IOException {
        byte[] sharedSecret = CryptManager.encryptData(getPublicKey(), getSecretKey().getEncoded());
        byte[] verifyToken = CryptManager.encryptData(getPublicKey(), getVerifyToken());
        writeVarInt(sharedSecret.length, out);
        out.write(sharedSecret);
        writeVarInt(verifyToken.length, out);
        out.write(verifyToken);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) throws IOException { }
}
