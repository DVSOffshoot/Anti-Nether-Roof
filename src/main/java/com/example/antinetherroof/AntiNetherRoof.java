package com.example.antinetherroof;

import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class AntiNetherRoof extends JavaPlugin implements org.bukkit.command.TabExecutor {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        getCommand("antinetherroof").setExecutor(this);
        getCommand("antinetherroof").setTabCompleter(this);

        getLogger().info("Anti Nether Roof enabled!");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (player.hasPermission("antinetherroof.bypass")) {
                        continue;
                    }

                    if (player.getWorld().getEnvironment() == World.Environment.NETHER) {

                        if (player.getLocation().getY() > 127) {
                            punish(player);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void punish(Player player) {
        String punishment = config.getString("punishment", "KILL").toUpperCase();

        switch (punishment) {

            case "KILL":
                player.setHealth(0);
                break;

            case "KICK":
                Location kickLocation = player.getLocation();
                kickLocation.setY(100);
                player.teleport(kickLocation);

                player.kickPlayer(
                        config.getString("kick-message", "You are not allowed on the Nether roof!")
                );
                break;

            case "TELEPORT":
                Location teleportLocation = player.getLocation();
                teleportLocation.setY(100);
                player.teleport(teleportLocation);
                break;

            case "BAN":
                Bukkit.getBanList(BanList.Type.NAME).addBan(
                        player.getName(),
                        config.getString("ban-message", "You have been banned for entering the Nether roof!"),
                        null,
                        "Anti Nether Roof"
                );

                player.kickPlayer(
                        config.getString("ban-message", "You have been banned for entering the Nether roof!")
                );
                break;

            default:
                player.setHealth(0);
                break;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("antinetherroof")) {

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("antinetherroof.reload")) {
                    sender.sendMessage("You do not have permission.");
                    return true;
                }

                reloadConfig();
                config = getConfig();

                sender.sendMessage("Anti Nether Roof config reloaded!");
                return true;
            }

            sender.sendMessage("Usage: /antinetherroof reload");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("antinetherroof")) {

            if (args.length == 1) {

                if ("reload".startsWith(args[0].toLowerCase())) {
                    return Collections.singletonList("reload");
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void onDisable() {
        getLogger().info("Anti Nether Roof disabled!");
    }
}