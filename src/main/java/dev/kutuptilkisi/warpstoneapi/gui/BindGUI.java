package dev.kutuptilkisi.warpstoneapi.gui;

import dev.kutuptilkisi.warpstoneapi.WarpStoneAPI;
import dev.kutuptilkisi.warpstoneapi.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class BindGUI {

    public final static HashMap<Inventory, UUID> openGUI = new HashMap<>();
    public final static HashMap<Inventory, Block> blocks = new HashMap<>();

    public BindGUI(WarpStoneAPI api, Player player, Block block){
        Inventory inv = Bukkit.createInventory(null, 9, api.getMessages().getColorString("binding-gui-name"));

        ItemStack slotOne = new ItemStack(block.getType());
        ItemMeta slotOneMeta = slotOne.getItemMeta();
        slotOneMeta.setDisplayName(ChatColor.GOLD + "Bind To Slot 1");
        slotOne.setItemMeta(slotOneMeta);

        ItemStack slotTwo = new ItemStack(block.getType());
        ItemMeta slotTwoMeta = slotOne.getItemMeta();
        slotTwoMeta.setDisplayName(ChatColor.GOLD + "Bind To Slot 2");
        slotTwo.setItemMeta(slotTwoMeta);

        inv.setItem(0, slotOne);
        inv.setItem(8, slotTwo);

        openGUI.put(inv, player.getUniqueId());
        blocks.put(inv, block);

        player.openInventory(inv);
    }

    public static void closeGUI(Inventory inv){
        openGUI.remove(inv);
        blocks.remove(inv);
    }

}
