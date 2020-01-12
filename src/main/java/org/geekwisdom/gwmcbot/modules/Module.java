/* * *************************************************************************************
' Script Name: GWmcbot.java
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

package org.geekwisdom.gwmcbot.modules;

import org.geekwisdom.gwmcbot.GWmcbot;

public abstract class Module {

    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
        onEnable();
        GWmcbot.getLog().info("Module \"" + this.getClass().getSimpleName() + "\" enabled!");
    }

    public void disable() {
        this.enabled = false;
        onDisable();
        GWmcbot.getLog().info("Module \"" + this.getClass().getSimpleName() + "\" disabled!");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
