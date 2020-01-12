/* * *************************************************************************************
' Script Name: DiscordDetalis.java
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
package org.geekwisdom.gwmcbot.io.discord;

import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;

public class DiscordMessageDispatcher {

    private TemmieWebhook webHook;

    public DiscordMessageDispatcher(String webHook) {
        this.webHook = new TemmieWebhook(webHook);
    }

    public void dispatchMessage(String content, DiscordDetails details) {
        DiscordMessage.DiscordMessageBuilder builder = DiscordMessage.builder()
                .username(details.getUserName())
                .content(content);
        if (details.getAvatar() != null && !details.getAvatar().isEmpty()) builder.avatarUrl(details.getAvatar());

        webHook.sendMessage(builder.build());
    }
}
