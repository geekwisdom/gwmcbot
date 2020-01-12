/* * *************************************************************************************
' Script Name: ChatCommandModule.java
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


import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.EventHandler;
import org.geekwisdom.gwmcbot.event.Listener;
import org.geekwisdom.gwmcbot.event.play.ChatEvent;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutChat;

import java.util.Scanner;

public class ChatProxyModule extends Module implements Listener {

    private Thread chatThread;

    @Override
    public void onEnable() {
        GWmcbot.getInstance().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(!chatThread.isInterrupted()){
                String line = scanner.nextLine();
                GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat(line));
            }
        });
        chatThread.start();
    }

    @Override
    public void onDisable() {
        chatThread.interrupt();
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText()))
            GWmcbot.getLog().info("[CHAT] " + event.getText());
    }
}
