package Blink.project.Abilities;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Shulker implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public Shulker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.shulk", true);
        }

        if (!player.hasPermission("auro.shulk")) {
            return;
        }

        player.getAttribute(Attribute.ARMOR).setBaseValue(20f);
    }

    @EventHandler
    public void EntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.SHULKER_BULLET || event.getEntityType() == EntityType.SHULKER) { // Add more hostile mobs as needed
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                if (isProtected(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isProtected(player)) {
                if (random.nextInt(5) == 0) { // 1 in 5 chance
                    teleportPlayerRandomly(player, 20);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (isProtected(player) && event.getEntity() instanceof LivingEntity) {
                LivingEntity damagedEntity = (LivingEntity) event.getEntity();
                damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 1));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (isProtected(player)) {
                EquipmentSlot slotType = EquipmentSlot.HAND;
                switch (event.getSlotType()) {
                    case ARMOR:
                    case QUICKBAR:
                        slotType = EquipmentSlot.HAND;
                        break;
                    default:
                        break;
                }
                if (slotType == EquipmentSlot.HAND) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isProtected(Player player) {
        return player.hasPermission("auro.shulk");
    }

    private void teleportPlayerRandomly(Player player, int radius) {
        Location currentLocation = player.getLocation();
        double xOffset = (random.nextDouble() * radius * 2) - radius;
        double zOffset = (random.nextDouble() * radius * 2) - radius;
        Location newLocation = currentLocation.clone().add(xOffset, 0, zOffset);

        // Ensure the new location is safe for teleportation
        newLocation = player.getWorld().getHighestBlockAt(newLocation).getLocation().add(0, 1, 0);

        player.teleport(newLocation);
    }
}