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

public class ChatCommandModule extends Module implements Listener {



    @Override
    public void onEnable() {
       GWmcbot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        GWmcbot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && event.getText().contains("Skeleton")) {
	   GWmcbot.getInstance().setSkeleton();
	   GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("Look Out !!"));
        }

	if (isEnabled() && event.getText().contains("Hello?")) {
            GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("I am stuck, somebody please help me."));
        }
    }
}
