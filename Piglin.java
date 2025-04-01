package Blink.project.Abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Piglin implements Listener {

    private final JavaPlugin plugin;
    private final List<String> allowedPlayers = Arrays.asList("Goannas");
    private final List<UUID> cursedPlayers = new ArrayList<>();  // List to store cursed players

    public Piglin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.piglin", true);
        }

        if (!player.hasPermission("auro.piglin")) {
            return;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!player.hasPermission("auro.piglin")) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();

        // Add attacker to list of cursed players
        if (!cursedPlayers.contains(attacker.getUniqueId())) {
            cursedPlayers.add(attacker.getUniqueId());

        }
    }

    // Method to check if a player is cursed
    public boolean isPlayerCursed(Player player) {
        return cursedPlayers.contains(player.getUniqueId());
    }

    // Method to remove a player from the cursed list
    public void removeCursedPlayer(Player player) {
        cursedPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.PIGLIN || event.getEntityType() == EntityType.ZOMBIFIED_PIGLIN || event.getEntityType() == EntityType.PIGLIN_BRUTE) { // Add more hostile mobs as needed
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.piglin");
    }

}