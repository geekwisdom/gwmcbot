/* * *************************************************************************************
' Script Name: LogFormatter.java
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

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    private final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

    @Override
    public synchronized String format(LogRecord lr) {
        return String.format(FORMAT,
                new Date(lr.getMillis()),
                lr.getLevel().getLocalizedName(),
                lr.getMessage()
        );
    }
}
