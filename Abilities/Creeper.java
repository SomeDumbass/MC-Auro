package Blink.project.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class Creeper implements Listener {

    private final JavaPlugin plugin;

    public Creeper(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Add an array of player names
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.creeper", true);
            // Add explosion knockback resistance attribute
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            if (attribute != null) {
                attribute.setBaseValue(1.0); // Maximum resistance
            }
            // Add explosion resistance potion effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, false, false));
        }

        if (!player.hasPermission("auro.creeper")) {
            return;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (allowedPlayers.contains(player.getName())) {
                // Play creeper hurt sound instead of player hurt sound
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0F, 1.0F);
                // Scale explosion damage lower
                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    event.setDamage(event.getDamage() * 0.5); // Reduce explosion damage by 50%
                }
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (allowedPlayers.contains(player.getName()) && event.getEntity().getType() == EntityType.CREEPER) {
                // Cancel the event if the target is a player and the entity is a creeper
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (allowedPlayers.contains(player.getName())) {
            // Create an explosion at the player's location
            player.getWorld().createExplosion(player.getLocation(), 4.0F, false, false);
        }
    }
}
