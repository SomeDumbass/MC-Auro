package Blink.project.Abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Aquatic implements Listener {

    private final JavaPlugin plugin;

    public Aquatic(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //Add an array of player names
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void Join(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.aquatic", true);
        }

        if (!player.hasPermission("auro.aquatic")) {
            return;
        }
    }
}
