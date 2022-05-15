package dev.kutuptilkisi.warpstoneapi.listener;

import dev.kutuptilkisi.warpstoneapi.WarpStoneAPI;
import dev.kutuptilkisi.warpstoneapi.gui.BindGUI;
import dev.kutuptilkisi.warpstoneapi.util.LocationUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class WarpStoneListener implements Listener {

    private final WarpStoneAPI main;
    private final HashMap<UUID, Long> cooldown;

    public WarpStoneListener(WarpStoneAPI main){
        this.main = main;
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem() != null){
            ItemStack item = e.getItem();
            if(item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("bind-item-name")))){
                if(main.isPillar(e.getClickedBlock().getLocation())){
                    setupSlot(e.getPlayer());
                    new BindGUI(main, e.getPlayer(), e.getClickedBlock());
                }
            }
        } else if(e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getItem() != null){
            if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("bind-item-name")))){
                setupSlot(e.getPlayer());

                String slot = e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "slot"), PersistentDataType.STRING);

                if(e.getPlayer().isSneaking()){
                    String newSlot, oldSlot;

                    ItemStack hand = e.getItem();
                    ItemMeta meta = hand.getItemMeta();
                    if(slot.equals("1")){
                        oldSlot = "1";
                        newSlot = "2";
                        meta.getPersistentDataContainer().set(new NamespacedKey(main, "slot"), PersistentDataType.STRING, "2");
                    } else {
                        oldSlot = "2";
                        newSlot = "1";
                        meta.getPersistentDataContainer().set(new NamespacedKey(main, "slot"), PersistentDataType.STRING, "1");
                    }
                    hand.setItemMeta(meta);
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(main.getMessages().getColorString("slot-change-message").replace("%new_slot%", newSlot).replace("%old_slot%", oldSlot)));
                    return;
                }

                String strloc;
                if(slot.equals("1")){
                    strloc = e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "slot1"), PersistentDataType.STRING);
                } else {
                    strloc = e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "slot2"), PersistentDataType.STRING);
                }
                if(strloc == null){
                    e.getPlayer().sendMessage(main.getMessages().getColorString("slot-not-binded"));
                    return;
                }
                Location loc = LocationUtil.getLocationFromString(strloc.split(",")[0]);
                if(!checkBeforeTeleport(e.getPlayer(), LocationUtil.getLocationFromString(strloc.split(",")[1]), slot)) return;

                if(cooldown.containsKey(e.getPlayer().getUniqueId())){
                    Long elapsed = System.currentTimeMillis() - cooldown.get(e.getPlayer().getUniqueId());
                    Long cooldownTime = main.getConfig().getInt("teleport-cooldown") * 1000L;
                    if(elapsed < cooldownTime){
                        e.getPlayer().sendMessage(main.getMessages().getColorString("cooldown-message").replace("%time%", String.valueOf((cooldownTime - elapsed) / 1000L)));
                        return;
                    }
                }

                e.getPlayer().sendMessage(main.getMessages().getColorString("teleport-message"));
                cooldown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.getPlayer().teleport(loc);
                    }
                }.runTaskLater(main, main.getConfig().getInt("teleport-wait") * 20L);

            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(BindGUI.openGUI.containsKey(e.getInventory()) && e.getClickedInventory() == e.getInventory() && e.getCurrentItem() != null){
            Location loc = BindGUI.blocks.get(e.getInventory()).getLocation();

            Player player = Bukkit.getPlayer(BindGUI.openGUI.get(e.getInventory()));
            assert player != null;

            ItemStack item = e.getCurrentItem();
            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemMeta handMeta = hand.getItemMeta();

            NamespacedKey key;
            if(item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Bind To Slot 1")){
                key = new NamespacedKey(main, "slot1");
            } else {
                key = new NamespacedKey(main, "slot2");
            }
            handMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, LocationUtil.getStringFromLocation(player.getLocation()) + "," + LocationUtil.getStringFromLocation(loc));
            hand.setItemMeta(handMeta);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        BindGUI.closeGUI(e.getInventory());
    }

    private void setupSlot(Player player){
        NamespacedKey key = new NamespacedKey(main, "slot");
        if(player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) == null){
            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemMeta handMeta = hand.getItemMeta();
            handMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "1");
            hand.setItemMeta(handMeta);
        }
    }

    private boolean checkBeforeTeleport(Player player, Location location, String slot){
        if(main.isPillar(location)){
            return true;
        } else {
            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemMeta handMeta = hand.getItemMeta();
            handMeta.getPersistentDataContainer().remove(new NamespacedKey(main, "slot"+slot));
            hand.setItemMeta(handMeta);
            player.sendMessage(main.getMessages().getColorString("pillar-not-exist"));
        }
        return false;
    }
}
