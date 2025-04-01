package Blink.project.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Axolotl implements Listener {

    private final JavaPlugin plugin;

    public Axolotl(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // List of allowed players (case-insensitive)
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Grant permission to allowed players
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.axolotl", true);
        }

        // Check if the player has the axolotl permission
        if (!player.hasPermission("auro.axolotl")) {
            return;
        }

        // Add permanent water breathing and regeneration effects to the player
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, false, false));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("auro.axolotl")) {
            return;
        }

        // Healing radius effect
        double healingRadius = 5.0;
        int healingAmount = 1;
        for (Entity entity : player.getNearbyEntities(healingRadius, healingRadius, healingRadius)) {
            if (entity instanceof Player nearbyPlayer && nearbyPlayer.getHealth() < nearbyPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                nearbyPlayer.setHealth(Math.min(nearbyPlayer.getHealth() + healingAmount, nearbyPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                nearbyPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You feel the healing presence of an Axolotl!");
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (allowedPlayers.contains(player.getName())) {
                // Reduce damage if the player is in water
                if (player.getLocation().getBlock().isLiquid()) {
                    event.setDamage(event.getDamage() * 0.5); // Reduce damage by 50%
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (allowedPlayers.contains(player.getName()) && (event.getEntityType() == EntityType.DROWNED || event.getEntityType() == EntityType.GUARDIAN)) {
                // Cancel the event if the target is a player and the entity is a water mob
                event.setCancelled(true);
            }
        }
    }
}
