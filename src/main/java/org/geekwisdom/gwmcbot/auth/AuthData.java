/* * *************************************************************************************
' Script Name: AuthData.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the main routine Minecraft Bot.
' @(#)    Here we simply instantion the bot and call it's 'start()' routine
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2019-07-23 - Initial Architecture
' 
' ************************************************************************************** */

/*
 * Orginal Code Forked from code created by David Luedtke (MrKinau)
 * 2019/5/3
 */

package org.geekwisdom.gwmcbot.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthData {

    @Getter private String accessToken;
    @Getter private String clientToken;
    @Getter private String profile;
    @Getter private String username;
}
