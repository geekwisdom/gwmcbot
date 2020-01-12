/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

/*
 * Created by Summerfeeling on May, 5th 2019
 */

package org.geekwisdom.gwmcbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.play.UpdateExperienceEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;

public class PacketInSetExperience extends Packet {
	
	@Getter private int experience;
	@Getter private int level;
	
	@Override
	public void write(ByteArrayDataOutput out, int protocolId) {
		//Only incoming packet
	}
	
	@Override
	public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
		in.readFloat();	//XP bar (useless)
		level = readVarInt(in);
		experience = readVarInt(in);

		GWmcbot.getInstance().getEventManager().callEvent(new UpdateExperienceEvent(experience, level));
	}
}
