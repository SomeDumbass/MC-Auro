package Blink.project.Abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Feline implements Listener {

    private final JavaPlugin plugin;
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    public Feline(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.feline", true);
        }

        if (!player.hasPermission("auro.feline")) {
            return;
        }

        // Grant feline abilities
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.feline")) {
            // Handle feline-specific movement abilities, if any
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.feline")) {
            // Handle feline-specific sneak abilities, if any
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.hasPermission("auro.feline") && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true); // No fall damage for feline players
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (player.hasPermission("auro.feline") && event.getEntityType() == EntityType.CREEPER) {
                event.setCancelled(true); // Creepers are scared of feline players
                
                // Make creepers run away from feline players
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getEntity().getLocation().distance(player.getLocation()) <= 10) {
                            event.getEntity().setVelocity(player.getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply(-1));
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks)
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.hasPermission("auro.feline") && !isMeat(event.getItem().getType())) {
                event.setCancelled(true); // Feline players can only eat meat
            }
        }
    }

    private boolean isMeat(Material material) {
        // Check if the food item is meat
        return switch (material) {
            case BEEF, COOKED_BEEF, PORKCHOP, COOKED_PORKCHOP, CHICKEN, COOKED_CHICKEN, MUTTON, COOKED_MUTTON, RABBIT, COOKED_RABBIT, COD, COOKED_COD, SALMON, COOKED_SALMON -> true;
            default -> false;
        };
    }
}
