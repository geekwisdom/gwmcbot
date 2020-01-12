/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.play.ChatEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;
import org.geekwisdom.gwmcbot.network.utils.TextComponent;

@NoArgsConstructor
public class PacketInChat extends Packet {

	@Getter private String text;
	private final JsonParser PARSER = new JsonParser();

	@Override
	public void write(ByteArrayDataOutput out, int protocolId) {
		//Only incoming packet
	}
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		this.text = readString(in);
		try {
			JsonObject object = PARSER.parse(text).getAsJsonObject();

			try {
				this.text = TextComponent.toPlainText(object);
			} catch (Exception ignored) {
				//Ignored
			}

			GWmcbot.getInstance().getEventManager().callEvent(new ChatEvent(getText()));
		} catch (Exception ignored) {
			//Ignored
		}
	}
}
