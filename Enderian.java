package Blink.project.Abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Enderian implements Listener {

    private final JavaPlugin plugin;
    public Enderian(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    private final List<String> allowedPlayers = Arrays.asList("Goannas");
    private final Set<Player> playersInWater = new HashSet<>();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.ender", true);
        }

        if (!player.hasPermission("auro.ender")) {
            return;
        }
    }


    private void teleportToCrosshair(Player player) {

        Block targetBlock = player.getTargetBlock(null, 125);
        if (targetBlock != null && targetBlock.getType() != Material.AIR && player.getFoodLevel() > 6) {
            Location startLocation = player.getLocation();
            Location teleportLocation = targetBlock.getLocation().add(0.5, 0, 0.5);
            float yaw = player.getLocation().getYaw(); // Current yaw (horizontal rotation)
            float pitch = player.getLocation().getPitch(); // Current pitch (vertical rotation)
            teleportLocation.setYaw(yaw);
            teleportLocation.setPitch(pitch);
            player.getWorld().spawnParticle(Particle.PORTAL, startLocation, 50, 0.5, 1, 0.5, 0.1);
            player.teleport(teleportLocation);
            player.getWorld().spawnParticle(Particle.PORTAL, teleportLocation, 50, 0.5, 1, 0.5, 0.1);
            player.playSound(startLocation, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f); // At start
            player.playSound(teleportLocation, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f); // At destination
        }
    }

    @EventHandler
    public void Consume(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("auro.ender")) {
            return;
        }

        teleportToCrosshair(player);

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("auro.ender")) {
            return;
        }

        if (player.isInWater()) {
            if (!playersInWater.contains(player)) {
                playersInWater.add(player);
            }

            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (playersInWater.contains(player) && player.isInWater()) {
                    player.damage(1); // Apply 1 heart of damage
                } else {
                    stopWaterDamage(player); // Stop applying damage if the player is no longer in water
                }
            }, 0L, 20L); // Run every second (20 ticks)
        } else {
            stopWaterDamage(player);
        }
    }

    private void stopWaterDamage(Player player) {
        if (playersInWater.contains(player)) {
            playersInWater.remove(player);
        }
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN || event.getEntityType() == EntityType.ENDERMITE) { // Add more hostile mobs as needed
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.ender");
    }

}
