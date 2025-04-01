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

import java.util.List;
import java.util.stream.Collectors;

public class Arachnid implements Listener {

    private final JavaPlugin plugin;

    public Arachnid(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // List of allowed players (case-insensitive)
    private final List<String> allowedPlayers = List.of("Goannas");

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && damager.hasPermission("auro.arachnid")) {
            Entity damagedEntity = event.getEntity();
            if (damagedEntity instanceof LivingEntity) {
                List<Creature> nearbySpiders = getHostileMobsWithinRadius(damagedEntity, 20);
                nearbySpiders.forEach(spider -> spider.setTarget((LivingEntity) damagedEntity));
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Spider && event.getTarget() instanceof Player player && isProtected(player)) {
            event.setCancelled(true);
        }
    }

    private boolean isProtected(Player player) {
        return player.hasPermission("auro.arachnid");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.arachnid", true);
        }
    }

    public static List<Creature> getHostileMobsWithinRadius(Entity entity, double radius) {
        Location location = entity.getLocation();
        return location.getWorld().getNearbyEntitiesByType(Creature.class, location, radius, radius, radius)
                .stream()
                .filter(e -> e instanceof Spider)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.arachnid") && player.isSneaking() && isOnWall(player)) {
            Vector velocity = player.getVelocity();
            velocity.setY(0.2); 
            player.setVelocity(velocity);
        }
    }

    private boolean isOnWall(Player player) {
        Location location = player.getLocation();
        Material headBlock = location.clone().add(0, 1, 0).getBlock().getType();
        Material feetBlock = location.getBlock().getType();
        Material frontBlock = location.clone().add(player.getLocation().getDirection().multiply(0.5)).getBlock().getType();
        return !headBlock.isSolid() && !feetBlock.isSolid() && frontBlock.isSolid();
    }
}
