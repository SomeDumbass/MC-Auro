package Blink.project.Abilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Arachnid implements Listener {

    private final JavaPlugin plugin;

    public Arachnid(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // List of allowed players (case-insensitive)
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check if the damager is a player and has the "auro.arachnid" permission
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (!damager.hasPermission("auro.arachnid")) {
                return; // Exit if the player doesn't have the required permission
            }

            // Get the entity being damaged
            Entity damagedEntity = event.getEntity();

            // Ensure the damaged entity is a LivingEntity (e.g., player, mob)
            if (!(damagedEntity instanceof LivingEntity)) {
                return; // Only living entities can be targeted
            }

            // Get all spiders within a 20-block radius of the damaged entity
            List<Entity> nearbySpiders = getHostileMobsWithinRadius(damagedEntity, 20);

            // Make all nearby spiders target the damaged entity
            for (Entity spider : nearbySpiders) {
                if (spider instanceof Creature) { // Ensure the entity is a mob that can target others
                    Creature hostileMob = (Creature) spider;
                    hostileMob.setTarget((LivingEntity) damagedEntity); // Cast to LivingEntity
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        // Check if the entity is a hostile mob
        if (event.getEntityType() == EntityType.SPIDER || event.getEntityType() == EntityType.CAVE_SPIDER) { // Add more hostile mobs as needed

            // Check if the target is a player
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();

                // Prevent targeting for specific players
                if (isProtected(player)) {
                    event.setCancelled(true); // Cancel the targeting event
                }
            }
        }
    }

    private boolean isProtected(Player player) {
        // Example: Check for a specific permission or metadata
        return player.hasPermission("auro.arachnid");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Grant permission to allowed players
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.arachnid", true);
        }

    }

    public static List<Entity> getHostileMobsWithinRadius(Entity player, double radius) {
        Location playerLocation = player.getLocation();
        Collection<Entity> nearbyEntities = playerLocation.getWorld().getNearbyEntities(playerLocation, radius, radius, radius);

        // Filter the list to include only hostile mobs
        List<Entity> hostileMobs = new ArrayList<>();
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Spider) { // Creatures include Zombies, Skeletons, etc.
                hostileMobs.add(entity);
            }
        }

        return hostileMobs;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player has permission
        if (!player.hasPermission("auro.arachnid")) {
            return;
        }

        // Check if the player is sneaking and moving slowly (indicating wall contact)
        if (player.isSneaking() && isOnWall(player)) {
            // Apply upward velocity to simulate climbing
            Vector velocity = player.getVelocity();
            velocity.setY(0.2); // Adjust this value for climbing speed
            player.setVelocity(velocity);
        }

    }

    /**
     * Checks if the player is on a wall by analyzing nearby blocks.
     */
    private boolean isOnWall(Player player) {
        Location location = player.getLocation();
        Material headBlock = location.clone().add(0, 1, 0).getBlock().getType(); // Block above the player
        Material feetBlock = location.getBlock().getType(); // Block at the player's feet

        // Check blocks in front of the player
        Vector direction = player.getLocation().getDirection();
        Location frontLocation = location.clone().add(direction.multiply(0.5));
        Material frontBlock = frontLocation.getBlock().getType();

        // Allow climbing if the player is near a solid block but not inside one
        return !headBlock.isSolid() && !feetBlock.isSolid() && frontBlock.isSolid();
    }
}