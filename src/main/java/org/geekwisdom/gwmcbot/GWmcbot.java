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

package org.geekwisdom.gwmcbot;

import lombok.Getter;
import lombok.Setter;
import org.geekwisdom.gwmcbot.auth.AuthData;
import org.geekwisdom.gwmcbot.auth.Authenticator;
import org.geekwisdom.gwmcbot.bot.Player;
import org.geekwisdom.gwmcbot.event.EventManager;
import org.geekwisdom.gwmcbot.mcitems.ItemHandler;
import org.geekwisdom.gwmcbot.io.LogFormatter;
import org.geekwisdom.gwmcbot.io.SettingsConfig;
import org.geekwisdom.gwmcbot.io.discord.DiscordMessageDispatcher;
import org.geekwisdom.gwmcbot.modules.*;
import org.geekwisdom.gwmcbot.network.ping.ServerPinger;
import org.geekwisdom.gwmcbot.network.protocol.NetworkHandler;
import org.geekwisdom.gwmcbot.network.protocol.ProtocolConstants;
import org.geekwisdom.gwmcbot.realms.RealmsAPI;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GWmcbot {

    public static final String PREFIX = "GeekWisdom.org Minecraft Chatbot 1.0";
    @Getter private static GWmcbot instance;
    @Getter public static Logger log = Logger.getLogger(GWmcbot.class.getSimpleName());

    @Getter @Setter private boolean running;
    @Getter private SettingsConfig config;
    @Getter private DiscordMessageDispatcher discord;
    @Getter @Setter private int serverProtocol = ProtocolConstants.MINECRAFT_1_8; //default 1.8
    @Getter @Setter private String serverHost;
    @Getter @Setter private int serverPort;
    @Getter @Setter private AuthData authData;
    @Getter @Setter private boolean wontConnect = false;
    @Getter         private EventManager eventManager;
    @Getter         private Player player;
    @Getter         private ClientDefaultsModule clientModule;

    @Getter         private Socket socket;
    @Getter         private NetworkHandler net;

    @Getter @Setter private McbotModule mcbotModule;

  		    private int Skeleton=0;
    private File logsFolder = new File("logs");

    public void setSkeleton()
   {
  Skeleton=1;  
}

   public int getSkeleton()
	{
	return Skeleton;
	}

   //Constructor
    public GWmcbot() {
        instance = this;

        //Initialize Logger
        log.setLevel(Level.ALL);
        ConsoleHandler ch;
        log.addHandler(ch = new ConsoleHandler());
        log.setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ch.setFormatter(formatter);

        //Generate/Load config
        this.config = new SettingsConfig();

        //Set logger file handler
        try {
            FileHandler fh;
            if(!logsFolder.exists() && !logsFolder.mkdir() && logsFolder.isDirectory())
                throw new IOException("Could not create logs folder!");
            log.addHandler(fh = new FileHandler("logs/log%g.log", 0 /* 0 = infinity */, getConfig().getLogCount()));
            fh.setFormatter(new LogFormatter());
        } catch (IOException e) {
            System.err.println("Could not create log!");
            System.exit(1);
        }

        //Authenticate player if online-mode is set
        if(getConfig().isOnlineMode())
            authenticate();
        else {
            getLog().info("Starting in offline-mode with username: " + getConfig().getUserName());
            this.authData = new AuthData(null, null, null, getConfig().getUserName());
        }

        String ip = getConfig().getServerIP();
        int port = getConfig().getServerPort();

        //Check rather to connect to realm
        if (getConfig().getRealmId() != -1) {
            RealmsAPI realmsAPI = new RealmsAPI(getAuthData());
            if (getConfig().getRealmId() == 0) {
                realmsAPI.printPossibleWorlds();
                GWmcbot.getLog().info("Shutting down, because realm-id is not set...");
                System.exit(0);
            }
            if (getConfig().isRealmAcceptTos())
                realmsAPI.agreeTos();
            else {
                GWmcbot.getLog().severe("*****************************************************************************");
                GWmcbot.getLog().severe("If you want to use realms you have to accept the tos in the config.properties");
                GWmcbot.getLog().severe("*****************************************************************************");
                System.exit(0);
            }

            String ipAndPort = null;
            for (int i = 0; i < 5; i++) {
                ipAndPort = realmsAPI.getServerIP(getConfig().getRealmId());
                if (ipAndPort == null) {
                    GWmcbot.getLog().info("Trying to receive IP (Try " + (i + 1) + ")...");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }
            if (ipAndPort == null)
                System.exit(0);
            ip = ipAndPort.split(":")[0];
            port = Integer.parseInt(ipAndPort.split(":")[1]);
        }

        //Ping server
        getLog().info("Pinging " + ip + ":" + port + " with protocol of MC-" + getConfig().getDefaultProtocol());
        ServerPinger sp = new ServerPinger(ip, port, this);
        sp.ping();

        //Activate Discord webHook
        if(!getConfig().getWebHook().equalsIgnoreCase("false"))
            this.discord = new DiscordMessageDispatcher(getConfig().getWebHook());
    }

    public void start() {
        if(isRunning())
            return;
        connect();
    }

    private boolean authenticate() {
        Authenticator authenticator = new Authenticator(getConfig().getUserName(), getConfig().getPassword());
        AuthData authData = authenticator.authenticate();
        if(authData == null) {
            setAuthData(new AuthData(null, null, null, getConfig().getUserName()));
            return false;
        }
        setAuthData(authData);
        return true;
    }

    private void connect() {
        String serverName = getServerHost();
        int port = getServerPort();

        do {
            try {
                setRunning(true);
                if(isWontConnect()) {
                    setWontConnect(false);
                    ServerPinger sp = new ServerPinger(getServerHost(), getServerPort(), this);
                    sp.ping();
                    if(isWontConnect()) {
                        if(!getConfig().isAutoReconnect())
                            return;
                        try {
                            Thread.sleep(getConfig().getAutoReconnectTime() * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                }
                this.socket = new Socket(serverName, port);

                this.net = new NetworkHandler();

                //Load EventManager
                this.eventManager = new EventManager();

                this.mcbotModule = new McbotModule();
                getMcbotModule().enable();

                new HandshakeModule(serverName, port).enable();
                new LoginModule(getAuthData().getUsername()).enable();
                if (getConfig().isProxyChat())
                    new ChatProxyModule().enable();
                if(getConfig().isStartTextEnabled())
                    new ChatCommandModule().enable();
                this.clientModule = new ClientDefaultsModule();
                getClientModule().enable();
                new ItemHandler(getServerProtocol());
                this.player = new Player();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                while (running) {
                    try {
                        net.readData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getLog().warning("Could not receive packet! Shutting down!");
                        break;
                    }
                }
            } catch (IOException e) {
                getLog().severe("Could not start bot: " + e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.socket = null;
                this.mcbotModule = null;
                this.net = null;
            }
            if (getConfig().isAutoReconnect()) {
                getLog().info("mcbotBot restarts in " + getConfig().getAutoReconnectTime() + " seconds...");
                try {
                    Thread.sleep(getConfig().getAutoReconnectTime() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (getAuthData() == null) {
                    if (getConfig().isOnlineMode())
                        authenticate();
                    else {
                        getLog().info("Starting in offline-mode with username: " + getConfig().getUserName());
                        authData = new AuthData(null, null, null, getConfig().getUserName());
                    }
                }
            }
        } while (getConfig().isAutoReconnect());
    }
}
