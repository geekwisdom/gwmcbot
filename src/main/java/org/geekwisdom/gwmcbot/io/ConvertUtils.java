/* * *************************************************************************************
' Script Name: ConvertUtils.java
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
package org.geekwisdom.gwmcbot.io;

import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.mcitems.AnnounceType;

public class ConvertUtils {

    public static Object convert(String value, Class type) {
        if(type.isAssignableFrom(String.class)) {
            return value;
        } else if(type.isAssignableFrom(double.class)) {
            return Double.valueOf(value);
        } else if(type.isAssignableFrom(boolean.class)) {
            return Boolean.valueOf(value);
        } else if(type.isAssignableFrom(byte.class)) {
            return Byte.valueOf(value);
        } else if(type.isAssignableFrom(float.class)) {
            return Float.valueOf(value);
        } else if(type.isAssignableFrom(int.class)) {
            return Integer.valueOf(value);
        } else if(type.isAssignableFrom(long.class)) {
            return Long.valueOf(value);
        } else if(type.isAssignableFrom(AnnounceType.class)) {
            try {
                return AnnounceType.valueOf(value);
            } catch (IllegalArgumentException ex) {
                GWmcbot.getLog().warning("Could not find Announce-Type: " + value);
                ex.printStackTrace();
                return AnnounceType.ALL;
            }
        }
        return null;
    }
}
