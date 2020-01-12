/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package org.geekwisdom.gwmcbot.modules;

import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.network.protocol.State;
import org.geekwisdom.gwmcbot.network.protocol.handshake.PacketOutHandshake;

public class HandshakeModule extends Module {

    @Getter private String serverName;
    @Getter private int serverPort;

    public HandshakeModule(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void onEnable() {
        GWmcbot.getInstance().getNet().sendPacket(new PacketOutHandshake(serverName, serverPort));
        GWmcbot.getInstance().getNet().setState(State.LOGIN);
    }

    @Override
    public void onDisable() {
        GWmcbot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }
}
