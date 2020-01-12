/* * *************************************************************************************
' Script Name: FishingModule.java
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

package org.geekwisdom.gwmcbot.modules;

import lombok.Getter;
import lombok.Setter;
import org.geekwisdom.gwmcbot.GWmcbot;
import org.geekwisdom.gwmcbot.event.EventHandler;
import org.geekwisdom.gwmcbot.event.Listener;
import org.geekwisdom.gwmcbot.event.play.DifficultySetEvent;
import org.geekwisdom.gwmcbot.event.play.EntityVelocityEvent;
import org.geekwisdom.gwmcbot.event.play.SpawnObjectEvent;
import org.geekwisdom.gwmcbot.event.play.UpdateSlotEvent;
import org.geekwisdom.gwmcbot.mcitems.AnnounceType;
import org.geekwisdom.gwmcbot.network.protocol.Packet;
import org.geekwisdom.gwmcbot.network.protocol.ProtocolConstants;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutChat;
import org.geekwisdom.gwmcbot.network.protocol.play.PacketOutUseItem;
import org.geekwisdom.gwmcbot.network.utils.ByteArrayDataInputWrapper;
import org.geekwisdom.gwmcbot.network.utils.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

//    @Getter @Setter private McbotModule mcbotModule;
public class McbotModule extends Module implements Runnable, Listener {

    private static final List<Integer> FISH_IDS_1_14 = Arrays.asList(625, 626, 627, 628);
    private static final List<Integer> FISH_IDS_1_8 = Collections.singletonList(349);
    private static int firstround=1;

    @Getter private List<Item> possibleCaughtItems = new CopyOnWriteArrayList<>();

    @Getter @Setter private int currentBobber = -1;
    @Getter @Setter private short lastY = -1;
    @Getter @Setter private boolean trackingNextFishingId = false;
    @Getter @Setter private boolean trackingNextEntityMeta = false;
    @Getter @Setter long lastFish = System.currentTimeMillis();

    @Getter @Setter private int heldSlot;
    private int helpcounter=0;
    @Override
    public void onEnable() {
        GWmcbot.getInstance().getEventManager().registerListener(this);
        if (GWmcbot.getInstance().getConfig().isStuckingFixEnabled())
            new Thread(this).start();
    }

    @Override
    public void onDisable() {
        GWmcbot.getLog().warning("Tried to disable " + this.getClass().getSimpleName() + ", can not disable it!");
    }

    public void fish() {
//        GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("This is a test!"));
    }

    public boolean containsPossibleItem(int eid) {
        return getPossibleCaughtItems().stream().anyMatch(item -> item.getEid() == eid);
    }

    public void addPossibleMotion(int eid, int motX, int motY, int motZ) {
        getPossibleCaughtItems().forEach(item -> {
            if(item.getEid() == eid) {
                item.setMotX(motX);
                item.setMotY(motY);
                item.setMotZ(motZ);
            }
        });
    }

    private void getCaughtItem() {
        if(getPossibleCaughtItems().size() < 1)
            return;
        Item currentMax = getPossibleCaughtItems().get(0);
        int currentMaxMot = getMaxMot(currentMax);
        for (Item possibleCaughtItem : getPossibleCaughtItems()) {
            int mot = getMaxMot(possibleCaughtItem);
            if(mot > currentMaxMot) {
                currentMax = possibleCaughtItem;
                currentMaxMot = mot;
            }
        }

        //Clear mem
        getPossibleCaughtItems().clear();

        //Print to console (based on announcetype)
        logItem(currentMax,
                GWmcbot.getInstance().getConfig().getAnnounceTypeConsole(),
                GWmcbot.getLog()::info,
                GWmcbot.getLog()::info);

        //Print in mc chat (based on announcetype)
        logItem(currentMax,
                GWmcbot.getInstance().getConfig().getAnnounceTypeChat(),
                (String str) -> GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat(GWmcbot.PREFIX + str)),
                (String str) -> {
                    // Delay the enchant messages to arrive after the item announcement
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat(str));
                });
    }


    private String stringify(Item item) {
        return "Caught \"" + item.getName() + "\"";
    }

    private void logItem(Item item, AnnounceType noisiness, Consumer<String> announce, Consumer<String> announceEnchants) {
        if (noisiness == AnnounceType.NONE)
            return;
        else if (noisiness == AnnounceType.ALL)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ALL_BUT_FISH && !FISH_IDS_1_14.contains(item.getItemId()) && !FISH_IDS_1_8.contains(item.getItemId()))
            announce.accept(stringify(item));

        if (item.getEnchantments().isEmpty())
            return;

        if (noisiness == AnnounceType.ONLY_ENCHANTED)
            announce.accept(stringify(item));
        else if (noisiness == AnnounceType.ONLY_BOOKS && item.getItemId() == 779)
            announce.accept(stringify(item));
        if (noisiness == AnnounceType.ONLY_BOOKS && item.getItemId() != 779)
            return;

        if (!item.getEnchantments().isEmpty()) {
            for (Map<String, Short> enchantment : item.getEnchantments()) {
                enchantment.keySet().forEach(s -> {
                    String asText = "-> "
                            + s.replace("minecraft:", "").toUpperCase()
                            + " "
                            + getRomanLevel(enchantment.get(s));
                    announceEnchants.accept(asText);
                });
            }
        }
    }

    private String getRomanLevel(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "" + number;
        }
    }

    private int getMaxMot(Item item) {
        return Math.abs(item.getMotX()) + Math.abs(item.getMotY()) + Math.abs(item.getMotZ());
    }

    private void noFishingRod() {
        GWmcbot.getLog().severe("No fishing rod equipped. Retrying later!");
    }

    private void reFish(int id) {
        setTrackingNextFishingId(false);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException e) { }     //Prevent Velocity grabbed from flying hook
            setCurrentBobber(id);
        }).start();
    }

    @EventHandler
    public void onSetDifficulty(DifficultySetEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTrackingNextFishingId(true);
            GWmcbot.getInstance().getNet().sendPacket(new PacketOutUseItem(GWmcbot.getInstance().getNet()));
            GWmcbot.getLog().info("Starting fishing!");
        }).start();
    }

    @EventHandler
    public void onEntityVelocity(EntityVelocityEvent event) {
        addPossibleMotion(event.getEid(), event.getX(), event.getY(), event.getZ());
        if(getCurrentBobber() != event.getEid())
            return;

        switch (GWmcbot.getInstance().getServerProtocol()) {
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                GWmcbot.getInstance().getMcbotModule().fish();
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                if(Math.abs(event.getY()) > 350) {
                    GWmcbot.getInstance().getMcbotModule().fish();
                } else if(lastY == 0 && event.getY() == 0) {    //Sometimes Minecraft does not push the bobber down, but this workaround works good
                    GWmcbot.getInstance().getMcbotModule().fish();
                }
                break;
            }
        }

        lastY = event.getY();
    }

    @EventHandler
    public void onUpdateSlot(UpdateSlotEvent event) {
        if(event.getWindowId() != 0)
            return;
        if(event.getSlotId() != GWmcbot.getInstance().getPlayer().getHeldSlot())
            return;
        ByteArrayDataInputWrapper testFishRod = new ByteArrayDataInputWrapper(event.getSlotData().toByteArray().clone());
        int protocolId = GWmcbot.getInstance().getServerProtocol();
        if(protocolId < ProtocolConstants.MINECRAFT_1_13) {
            short itemId = testFishRod.readShort();
            if (itemId != 346)  //Normal ID
                noFishingRod();
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13) {
            short itemId = testFishRod.readShort();
            if (itemId != 563)  //ID in 1.13.0
                noFishingRod();
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_1) {
            short itemId = testFishRod.readShort();
            if (itemId != 568)  //ID in 1.13.1
                noFishingRod();
        } else if(protocolId == ProtocolConstants.MINECRAFT_1_13_2) {
            boolean present = testFishRod.readBoolean();
            if(!present)
                noFishingRod();
            int itemId = Packet.readVarInt(testFishRod);
            if (itemId != 568) //ID in 1.13.2
                noFishingRod();
        } else {
            boolean present = testFishRod.readBoolean();
            if(!present)
                noFishingRod();
            int itemId = Packet.readVarInt(testFishRod);
            if (itemId != 622) //ID in 1.14
                noFishingRod();
        }
    }

    @EventHandler
    public void onSpawnObject(SpawnObjectEvent event) {
        if(!GWmcbot.getInstance().getMcbotModule().isTrackingNextFishingId())
            return;
        switch (GWmcbot.getInstance().getServerProtocol()) {
            case ProtocolConstants.MINECRAFT_1_8:
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9: {
                if(event.getType() == 90) {   //90 = bobber
                    reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4: {
                if(event.getType() == 101) {   //101 = bobber
                    reFish(event.getId());
                }
                break;
            }
            case ProtocolConstants.MINECRAFT_1_15:
            default: {
                if(event.getType() == 102) {   //102 = bobber
                    reFish(event.getId());
                }
                break;
            }
        }
    }

    @Override
    public void run() {
        while (true) {

        if(System.currentTimeMillis() - getLastFish() > 60000) {
                setLastFish(System.currentTimeMillis());
                setCurrentBobber(-1);
                setTrackingNextEntityMeta(false);
                setTrackingNextFishingId(true);
                GWmcbot.getInstance().getNet().sendPacket(new PacketOutUseItem(GWmcbot.getInstance().getNet()));
                GWmcbot.getLog().warning("Bot is slow (maybe stuck). Trying to restart!");
            }
            try {
                Thread.sleep(5000);
                if (firstround == 1)
		{
	        firstround=0;
		GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("/teleport -80.7 59.0 -10.45"));    
		}
               else { 
		if (GWmcbot.getInstance().getSkeleton() == 0) 

			{			
			helpcounter++;
			if (helpcounter > 1000) {
 				GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("/tellraw @a[level=0] {\"text\":\"<GeekWisdom> Help Me!, Help Me!\"}"));    
				helpcounter=0;
				}
			}
                Thread.sleep(2500);
		// GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("/xp add @a[x=-80.7,y=59.0,z=-10.45,distance=..3] 100"));    
		GWmcbot.getInstance().getNet().sendPacket(new PacketOutChat("/tellraw @a[x=-80.7,y=59.0,z=-10.45,distance=..3] {\"text\":\"You found me! Good Job!!\"}"));    
		    }
    
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
