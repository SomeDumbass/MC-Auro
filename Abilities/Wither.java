package Blink.project.Abilities;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class Wither implements Listener {

    private final JavaPlugin plugin;

    public Wither(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.wither", true);
        }

        if (!player.hasPermission("auro.wither")) {
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.hasPermission("auro.wither")) {
                Entity entity = event.getEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1)); // 60 ticks = 3 seconds
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.wither")) {
            // Turn roses into wither roses within a 10-block radius
            for (int x = -10; x <= 10; x++) {
                for (int y = -10; y <= 10; y++) {
                    for (int z = -10; z <= 10; z++) {
                        Block block = player.getLocation().getBlock().getRelative(x, y, z);
                        if (block.getType() == Material.ROSE_BUSH) {
                            block.setType(Material.WITHER_ROSE);
                        }
                    }
                }
            }

            // Apply wither effect to players within a 5-block radius
            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player) {
                    Player nearbyPlayer = (Player) entity;
                    if (!nearbyPlayer.equals(player)) {
                        nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1)); // 60 ticks = 3 seconds
                    }
                }
            }
        }
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.WITHER_SKELETON) {
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.wither");
    }
}