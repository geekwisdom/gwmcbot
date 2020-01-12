/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 */

package org.geekwisdom.gwmcbot.modules;

import lombok.Getter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.EventHandler;
import org.geekwisdom.gwmcbot.event.Listener;
import org.geekwisdom.gwmcbot.event.login.EncryptionRequestEvent;
import org.geekwisdom.gwmcbot.event.login.LoginDisconnectEvent;
import org.geekwisdom.gwmcbot.event.login.LoginSuccessEvent;
import org.geekwisdom.gwmcbot.event.login.SetCompressionEvent;
import org.geekwisdom.gwmcbot.event.login.LoginPluginRequestEvent;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.State;
import org.geekwisdom.gwmcbot.network.protocol.login.PacketOutEncryptionResponse;
import org.geekwisdom.gwmcbot.network.protocol.login.PacketOutLoginStart;
import org.geekwisdom.gwmcbot.network.protocol.login.PacketOutLoginPluginResponse;
import org.geekwisdom.gwmcbot.network.utils.CryptManager;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;

public class LoginModule extends Module implements Listener {

    @Getter private String userName;

    public LoginModule(String userName) {
        this.userName = userName;
        GWmcbot.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onEnable() {
        GWmcbot.getInstance().getNet().sendPacket(new PacketOutLoginStart(getUserName()));
    }

    @Override
    public void onDisable() {
        GWmcbot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }

    @EventHandler
    public void onEncryptionRequest(EncryptionRequestEvent event) {
        NetworkHandler networkHandler = GWmcbot.getInstance().getNet();

        //Set public key
        networkHandler.setPublicKey(event.getPublicKey());

        //Generate & Set secret key
        SecretKey secretKey = CryptManager.createNewSharedKey();
        networkHandler.setSecretKey(secretKey);

        byte[] serverIdHash = CryptManager.getServerIdHash(event.getServerId().trim(), event.getPublicKey(), secretKey);
        if(serverIdHash == null) {
            GWmcbot.getLog().severe("Cannot hash server id: exiting!");
            GWmcbot.getInstance().setRunning(false);
            return;
        }

        String var5 = (new BigInteger(serverIdHash)).toString(16);
        String var6 = sendSessionRequest(GWmcbot.getInstance().getAuthData().getUsername(), "token:" + GWmcbot.getInstance().getAuthData().getAccessToken() + ":" + GWmcbot.getInstance().getAuthData().getProfile(), var5);

        networkHandler.sendPacket(new PacketOutEncryptionResponse(event.getServerId(), event.getPublicKey(), event.getVerifyToken(), secretKey));
        networkHandler.activateEncryption();
        networkHandler.decryptInputStream();
    }

    @EventHandler
    public void onLoginDisconnect(LoginDisconnectEvent event) {
        GWmcbot.getLog().severe("Login failed: " + event.getErrorMessage());
        GWmcbot.getInstance().setRunning(false);
        GWmcbot.getInstance().setAuthData(null);
    }

    @EventHandler
    public void onSetCompression(SetCompressionEvent event) {
        GWmcbot.getInstance().getNet().setThreshold(event.getThreshold());
    }

    @EventHandler
    public void onLoginPluginRequest(LoginPluginRequestEvent event) {
        GWmcbot.getInstance().getNet().sendPacket(new PacketOutLoginPluginResponse(event.getMsgId(), false, null));
    }

    @EventHandler
    public void onLoginSuccess(LoginSuccessEvent event) {
        GWmcbot.getLog().info("Login successful!");
        GWmcbot.getLog().info("Name: " + event.getUserName());
        GWmcbot.getLog().info("UUID: " + event.getUuid());
        GWmcbot.getInstance().getNet().setState(State.PLAY);
    }

    private String sendSessionRequest(String user, String session, String serverid) {
        try {
            return sendGetRequest("http://session.minecraft.net/game/joinserver.jsp"
                    + "?user=" + urlEncode(user)
                    + "&sessionId=" + urlEncode(session)
                    + "&serverId=" + urlEncode(serverid));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String sendGetRequest(String url) {
        try {
            URL var4 = new URL(url);
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.openStream()));
            String var6 = var5.readLine();
            var5.close();
            return var6;
        } catch (IOException var7) {
            return var7.toString();
        }
    }

    private String urlEncode(String par0Str) throws IOException {
        return URLEncoder.encode(par0Str, "UTF-8");
    }
}
