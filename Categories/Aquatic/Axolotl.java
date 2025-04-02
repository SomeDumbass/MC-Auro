package Blink.project.Abilities;

//Misc Imports
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

//Attribute Imports
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

//Entity Imports
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

//Event Essentials
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

//Entity Events
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

//Player events
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

//Potion Imports
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//Scheduler Imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

//Java utils
import java.util.Arrays;
import java.util.List;

public class Axolotl implements Listener {

    private final JavaPlugin plugin;

    public Axolotl (JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // List of players with access to this ability
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

  
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Grant permission to allowed players
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.axolotl", true);
        }

        // Check if the player has the [ability] permission
        if (!player.hasPermission("auro.axolotl")) {
            return;
        }

        //Increase underwater movement speed
        Attribute WaterSpeed = player.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
        double BaseWaterSpeed = WaterSpeed.getBaseValue();
        double MultWaterSpeed = (double) BaseWaterSpeed * 2.0d;
        player.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(MultWaterSpeed);
        player.sendMessage("Water speed set to " + String.valueOf(MultWaterSpeed));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Get nearby entities in a 10 block radius
        Collection<Entity> nearbyEntities = player.getNearbyEntities(10, 10, 10);
      
        // Iterate through nearby entities
        for (Entity entity : nearbyEntities) {
            // Ensure the entity is a living entity (e.g., players, animals, monsters)
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                PotionEffectType effectType = PotionEffectType.REGENERATION;
                livingEntity.addPotionEffect(new PotionEffect(effectType, 60, 2, false, false, false));
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("auro.axolotl")) {
            return;
        }

        if (player.isSneaking() && player.isInWater()) {
            double WaterGravity = 0d;
            player.getAttribute(Attribute.GRAVITY).setBaseValue(WaterGravity);
        } else if (!player.isSneaking()) {
            double DefaultGravity = player.getAttribute(Attribute.GRAVITY).getDefaultValue();
            player.getAttribute(Attribute.GRAVITY).setBaseValue(DefaultGravity);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (allowedPlayers.contains(player.getName()) && player.isInWater()) {
                double baseDamage = event.getDamage();
                double multDamage = (double) baseDamage * 0.5d;
                event.setDamage(multDamage);
            }
        }
    }
}
