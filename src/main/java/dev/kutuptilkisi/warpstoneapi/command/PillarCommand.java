package dev.kutuptilkisi.warpstoneapi.command;

import dev.kutuptilkisi.warpstoneapi.WarpStoneAPI;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PillarCommand implements CommandExecutor {

    private final WarpStoneAPI main;

    public PillarCommand(WarpStoneAPI main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;

            if(!player.hasPermission("pillars.edit")){
                player.sendMessage("You dont have enough perm to use this command");
                return false;
            }
            if(args.length == 1 && args[0].equalsIgnoreCase("create")){
                Block block = player.getTargetBlockExact(5);
                if(block == null){
                    player.sendMessage("Please look to a pillar");
                    return false;
                }

                main.makePillar(block.getLocation(), block);
            } else if(args.length == 1 && args[0].equalsIgnoreCase("delete")){
                Block block = player.getTargetBlockExact(5);
                if(block == null){
                    player.sendMessage("Please look to a pillar");
                    return false;
                }

                main.deletePillar(block.getLocation());
            } else if(args.length == 1 && args[0].equalsIgnoreCase("check")){
                Block block = player.getTargetBlockExact(5);
                if(block == null){
                    player.sendMessage("Please look to a pillar");
                    return false;
                }

                player.sendMessage(ChatColor.GREEN + Boolean.toString(main.isPillar(block.getLocation())));
            }
        } else {
            main.getLogger().info("You cant use this command");
        }

        return false;
    }
}
