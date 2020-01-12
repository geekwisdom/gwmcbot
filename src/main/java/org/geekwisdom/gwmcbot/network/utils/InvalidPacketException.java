/*
 * Created by David Luedtke (MrKinau)
 * 2019/11/2
 */

package org.geekwisdom.gwmcbot.network.utils;

public class InvalidPacketException extends RuntimeException {

    public InvalidPacketException(String msg) {
        super(msg);
    }
}
