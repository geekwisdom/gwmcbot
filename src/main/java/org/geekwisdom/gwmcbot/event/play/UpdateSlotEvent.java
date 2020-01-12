/* * *************************************************************************************
' Script Name: UpdateSlotEvent.java
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

package org.geekwisdom.gwmcbot.event.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.geekwisdom.gwmcbot.event.Event;

@AllArgsConstructor
public class UpdateSlotEvent extends Event {

    @Getter private int windowId;
    @Getter private short slotId;
    @Getter private ByteArrayDataOutput slotData;

}
