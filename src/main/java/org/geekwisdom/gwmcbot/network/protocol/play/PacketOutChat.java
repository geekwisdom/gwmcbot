/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.io.discord.DiscordDetails;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

@AllArgsConstructor
public class PacketOutChat extends Packet {

    @Getter private String message;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        writeString(getMessage(), out);
        if(!GWmcbot.getInstance().getConfig().getWebHook().equalsIgnoreCase("false"))
            GWmcbot.getInstance().getDiscord().dispatchMessage("`" + getMessage() + "`",
                    new DiscordDetails("GWmcbot", "https://vignette.wikia.nocookie.net/mcmmo/images/2/2f/FishingRod.png/revision/latest?cb=20110822134546"));

    }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) { }
}
