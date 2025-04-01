package Blink.project.Abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.List;

public class Slime implements Listener {

    private final JavaPlugin plugin;
    private static NamespacedKey SCALE_KEY;

    public Slime(JavaPlugin plugin) {
        this.plugin = plugin;
        SCALE_KEY = new NamespacedKey(plugin, "auro.slime.scale");
    }

    // Add an array of player names
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.slime", true);
        }

        if (!player.hasPermission("auro.slime")) {
            return;
        }

        // Load the player's scale from persistent data
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(SCALE_KEY, PersistentDataType.DOUBLE)) {
            double scale = data.get(SCALE_KEY, PersistentDataType.DOUBLE);
            AttributeInstance scaleAttribute = player.getAttribute(Attribute.SCALE);
            if (scaleAttribute != null) {
                scaleAttribute.setBaseValue(scale);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!player.hasPermission("auro.slime")) {
            return;
        }

        double damage = event.getFinalDamage();
        AttributeInstance scaleAttribute = player.getAttribute(Attribute.SCALE);

        if (scaleAttribute != null) {
            double currentScale = scaleAttribute.getBaseValue();
            double newScale = currentScale - damage * 0.005;
            scaleAttribute.setBaseValue(newScale);

            // Save the new scale to persistent data
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(SCALE_KEY, PersistentDataType.DOUBLE, newScale);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.SLIME) { // Add more hostile mobs as needed
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

                        @EventHandler
public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
        return;
    }

    Player player = (Player) event.getEntity();
    if (!player.hasPermission("auro.slime")) {
        return;
    }

    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
        // Cancel the fall damage
        event.setCancelled(true);

        // Apply a bounce effect
        player.setVelocity(player.getVelocity().setY(1.0));

        // Optionally, you can play a sound or particle effect here
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 1.0f, 1.0f);
    } else {
        double damage = event.getFinalDamage();
        AttributeInstance scaleAttribute = player.getAttribute(Attribute.SCALE);

        if (scaleAttribute != null) {
            double currentScale = scaleAttribute.getBaseValue();
            double newScale = currentScale - damage * 0.005;
            scaleAttribute.setBaseValue(newScale);

            // Save the new scale to persistent data
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(SCALE_KEY, PersistentDataType.DOUBLE, newScale);
        }

        event.setCancelled(true);
    }
}

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.slime");
    }
}
