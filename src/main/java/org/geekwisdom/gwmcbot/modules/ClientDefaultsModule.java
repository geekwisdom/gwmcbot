/* * *************************************************************************************
' Script Name: ClientDefaultsModule.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the main routine Minecraft Bot.
' @(#)    Here we simply instantion the bot and call it's 'start()' routine
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2020-01-20 Initial Version
' 
' ************************************************************************************** */

/*
 * Orginal Code Forked from code created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package org.geekwisdom.gwmcbot.modules;

import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.bot.Player;
import org.geekwisdom.gwmcbot.event.EventHandler;
import org.geekwisdom.gwmcbot.event.Listener;
import org.geekwisdom.gwmcbot.event.play.*;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutChat;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutClientSettings;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutKeepAlive;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutPosition;

import java.util.Arrays;

public class ClientDefaultsModule extends Module implements Listener {

    @Getter private Thread positionThread;

    @Override
    public void onEnable() {
        GWmcbot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        positionThread.interrupt();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Send start texts
            if(GWmcbot.getInstance().getConfig().isStartTextEnabled()) {
                Arrays.asList(GWmcbot.getInstance().getConfig().getStartText().split(";")).forEach(s -> {
                    GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("La Tee Dah!"));
                });
            }

            //Start position updates
            startPositionUpdate(GWmcbot.getInstance().getNet());
        }).start();
    }

    @EventHandler
    public void onDisconnect(DisconnectEvent event) {
        GWmcbot.getLog().info("Disconnected: " + event.getDisconnectMessage());
        GWmcbot.getInstance().setRunning(false);
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        GWmcbot.getInstance().getNet().sendPacket(new PacketOutClientSettings());
    }

    @EventHandler
    public void onKeepAlive(KeepAliveEvent event) {
        GWmcbot.getInstance().getNet().sendPacket(new PacketOutKeepAlive(event.getId()));
    }

    @EventHandler
    public void onUpdatePlayerList(UpdatePlayerListEvent event) {
        if(GWmcbot.getInstance().getConfig().isAutoDisconnect() && event.getPlayers().size() > GWmcbot.getInstance().getConfig().getAutoDisconnectPlayersThreshold()) {
            GWmcbot.getLog().warning("Max players threshold reached. Stopping");
            GWmcbot.getInstance().setWontConnect(true);
            GWmcbot.getInstance().setRunning(false);
        }
    }

    private void startPositionUpdate(NetworkHandler networkHandler) {
        if(positionThread != null)
            positionThread.interrupt();
        positionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Player player = GWmcbot.getInstance().getPlayer();
                networkHandler.sendPacket(new PacketOutPosition(player.getX(), player.getY(), player.getZ(), true));
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        });
        positionThread.start();
    }
}
