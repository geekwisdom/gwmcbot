/* * *************************************************************************************
' Script Name: Authenticator.java
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

package org.geekwisdom.gwmcbot.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.geekwisdom.gwmcbot.GWmcbot;

import java.io.IOException;

@AllArgsConstructor
public class Authenticator {

    private final String AUTH_SERVER = "https://authserver.mojang.com/authenticate";

    private String username;
    private String password;

    public AuthData authenticate() {
        JsonObject data = new JsonObject();
        JsonObject agent = new JsonObject();
        agent.addProperty("name", "minecraft");
        agent.addProperty("version", "1");
        data.add("agent", agent);
        data.addProperty("username", username);
        data.addProperty("password", password);

        try {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(AUTH_SERVER)
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .setEntity(new StringEntity(data.toString()))
                    .build();

            HttpResponse answer = HttpClientBuilder.create().build().execute(request);
            GWmcbot.getLog().info("Try to authenticate " + username + ":" + password.replaceAll(".", "*"));

            if (answer.getStatusLine().getStatusCode() != 200) {
                GWmcbot.getLog().severe("Authentication failed with code " + answer.getStatusLine().getStatusCode() + " from " + AUTH_SERVER + ": " + answer.getStatusLine());
                GWmcbot.getLog().severe(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
                return null;
            }

            JsonObject responseJson = (JsonObject) new JsonParser().parse(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
            String accessToken = responseJson.get("accessToken").getAsString();
            String clientToken = responseJson.get("clientToken").getAsString();
            String profile = responseJson.getAsJsonObject("selectedProfile").get("id").getAsString();
            String username = responseJson.getAsJsonObject("selectedProfile").get("name").getAsString();
            GWmcbot.getLog().info("Authentication successful!");
            return new AuthData(accessToken, clientToken, profile, username);
        } catch (IOException e) {
            GWmcbot.getLog().severe("Error while connecting to: " + AUTH_SERVER);
        }
        return null;
    }
}
