package Blink.project.Abilities;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Blaze implements Listener {

    private final JavaPlugin plugin;

    public Blaze(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // List of allowed players (case-insensitive)
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Grant permission to allowed players
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.blaze", true);
        }

        // Check if the player has the blaze permission
        if (!player.hasPermission("auro.blaze")) {
            return;
        }

        player.getAttribute(Attribute.ARMOR).setBaseValue(10);
        player.setAllowFlight(true);
        player.setFireTicks(Integer.MAX_VALUE);
        player.setFlySpeed(0.035f);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isProtected(player)) {
                if (event.getCause() == EntityDamageEvent.DamageCause.LAVA 
                    || event.getCause() == EntityDamageEvent.DamageCause.FIRE 
                    || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.BLAZE || event.getEntityType() == EntityType.MAGMA_CUBE) { // Add more hostile mobs as needed
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player has the blaze permission
        if (!player.hasPermission("auro.blaze")) {
            return;
        }

        // Check if the player right-clicked with an empty hand
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                // Check if the player has enough hunger
                if (player.getFoodLevel() >= 4) {
                    // Launch a fireball
                    player.launchProjectile(Fireball.class);

                    // Reduce the hunger by 2 bars (4 points)
                    player.setFoodLevel(player.getFoodLevel() - 4);
                } else {
                    player.sendMessage("You need at least 2 bars of hunger to use this ability!");
                }
            }
        }
    }

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.blaze");
    }
}
