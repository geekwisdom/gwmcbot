/* * *************************************************************************************
' Script Name: Player.java
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

package org.geekwisdom.gwmcbot.bot;


import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.Setter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.EventHandler;
import org.geekwisdom.gwmcbot.event.Listener;
import org.geekwisdom.gwmcbot.event.play.PosLookChangeEvent;
import org.geekwisdom.gwmcbot.event.play.SetHeldItemEvent;
import org.geekwisdom.gwmcbot.event.play.UpdateExperienceEvent;
import org.geekwisdom.gwmcbot.event.play.UpdateSlotEvent;
import org.geekwisdom.gwmcbot.mcitems.AnnounceType;
import org.geekwisdom.gwmcbot.network.protocol.ProtocolConstants;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutChat;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutTeleportConfirm;

public class Player implements Listener {

    @Getter @Setter private double x;
    @Getter @Setter private double y;
    @Getter @Setter private double z;
    @Getter @Setter private float yaw;
    @Getter @Setter private float pitch;

    @Getter @Setter private int experience;
    @Getter @Setter private int levels;

    @Getter @Setter private int heldSlot;
    @Getter @Setter private ByteArrayDataOutput slotData;

    public Player() {
        GWmcbot.getInstance().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onPosLookChange(PosLookChangeEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
        this.yaw = event.getYaw();
        this.pitch = event.getPitch();
        if (GWmcbot.getInstance().getServerProtocol() >= ProtocolConstants.MINECRAFT_1_9)
            GWmcbot.getInstance().getNet().sendPacket(new PacketOutTeleportConfirm(event.getTeleportId()));

    }

    @EventHandler
    public void onUpdateXP(UpdateExperienceEvent event) {
        if(getLevels() >= 0 && getLevels() < event.getLevel()) {
            if(GWmcbot.getInstance().getConfig().getAnnounceTypeConsole() != AnnounceType.NONE)
                GWmcbot.getLog().info("Achieved level " + event.getLevel());
            if(!GWmcbot.getInstance().getConfig().getAnnounceLvlUp().equalsIgnoreCase("false"))
                GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat(GWmcbot.getInstance().getConfig().getAnnounceLvlUp().replace("%lvl%", String.valueOf(event.getLevel()))));
        }

        this.levels = event.getLevel();
        this.experience = event.getExperience();
    }

    @EventHandler
    public void onSetHeldItem(SetHeldItemEvent event) {
        this.heldSlot = event.getSlot();
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if(event.getWindowId() != 0)
            return;
        if(event.getSlotId() != getHeldSlot())
            return;
        this.slotData = event.getSlotData();
    }
}
