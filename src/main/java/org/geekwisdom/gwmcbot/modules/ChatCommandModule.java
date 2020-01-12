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


    int FoundMe=0;   

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

	if (isEnabled() && event.getText().contains("Hello?")) {
            GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("I am stuck, somebody please help me."));
        }

	if (isEnabled() && event.getJson().contains("skeleton")) {
            GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("Watch Out!"));
           FoundMe=1;
          try { Thread.sleep(1500); } catch (Exception e) { e.printStackTrace(); }
           GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("You found me - Good Job!"));

        }

	else
		{
         // if (isEnabled())   GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("I heard you you said:" + event.getText()));
	 if (isEnabled()) System.out.println("I heard you you said:" + event.getText());
		}
    }
}
