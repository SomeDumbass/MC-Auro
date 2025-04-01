package Blink.project.Abilities;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Avian implements Listener {

    private final JavaPlugin plugin;
    private final Set<UUID> highPlayers = new HashSet<>();

    public Avian(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void Join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.avian", true);
        }

        if (!player.hasPermission("auro.avian")) {
            return;
        }

        // Equip wings on join if the player has permission
        equipWings(player);
    }

    /**
     * Creates the custom Elytra item (wings).
     */
    public ItemStack wings() {
        ItemStack wing = new ItemStack(Material.ELYTRA);
        ItemMeta meta = wing.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false); // Prevents unequipping
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        wing.setItemMeta(meta);
        return wing;
    }

    /**
     * Equips the wings to the player's chestplate slot.
     */
    private void equipWings(Player player) {
        player.getInventory().setChestplate(wings());
    }

    /**
     * Prevents the wings from being dropped on death.
     */
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Check if the player has permission for avian abilities
        if (!player.hasPermission("auro.avian")) {
            return;
        }

        // Remove the wings from the drops
        event.getDrops().removeIf(item -> item.getType() == Material.ELYTRA && item.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE));
    }

    /**
     * Re-adds the wings to the player's chestplate slot on respawn.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Check if the player has permission for avian abilities
        if (!player.hasPermission("auro.avian")) {
            return;
        }

        // Equip wings on respawn
        equipWings(player);
    }

    private static final double MAX_RANGE = 50;
    private static final double MAX_SPEED = 1.5;
    private static final double SMOOTHING_FACTOR = 4.0;

    @EventHandler
    public void Move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("auro.avian")) {
            return;
        }

        if (player.getLocation().getY() < 30) {

            if (!highPlayers.contains(player.getUniqueId())) {

                highPlayers.add(player.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!highPlayers.contains(player.getUniqueId())) {
                            cancel();
                            return;
                        }

                        player.setFreezeTicks(Integer.MAX_VALUE);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Critically low elevation warning"));

                    }
                }.runTaskTimer(plugin, 20L, 20L); // Run every 20 ticks (1 second)

            }

        } else {
            highPlayers.remove(player.getUniqueId());
            player.setFreezeTicks(0);
        }

        if (player.getFallDistance() > 5f) {
            player.setGliding(true);
        }

        if (player.isGliding()) {

            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection().normalize();

            Location targetLocation = null;
            for (double distance = 0; distance <= MAX_RANGE; distance += 0.1) {
                // Calculate the current position along the ray
                Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(distance));

                // Check if the current position intersects with a block
                if (!currentLocation.getBlock().isPassable()) {
                    targetLocation = currentLocation;
                    break;
                }
            }

            // If no block was hit, use the maximum range point in the air
            if (targetLocation == null) {
                targetLocation = eyeLocation.clone().add(direction.clone().multiply(MAX_RANGE));
            }

            // Calculate the direction vector from the player to the target
            Vector velocityDirection = targetLocation.toVector().subtract(player.getLocation().toVector()).normalize();

            // Base glide speed boost
            double glideSpeedBoost = 0.2; // Adjust this value to control the base speed boost

            // Get the player's current velocity
            Vector currentVelocity = player.getVelocity();
            double currentSpeed = currentVelocity.length();

            // Calculate the remaining speed difference
            double remainingSpeed = MAX_SPEED - currentSpeed;

            // Apply exponential decay to the speed boost as the player approaches max speed
            double speedFactor = Math.max(0, remainingSpeed / MAX_SPEED); // Normalize remaining speed
            speedFactor = Math.pow(speedFactor, SMOOTHING_FACTOR); // Apply smoothing factor

            // Scale the velocity boost based on the speed factor
            Vector velocityBoost = velocityDirection.multiply(glideSpeedBoost * speedFactor);

            // Add the velocity boost to the player's current velocity
            Vector newVelocity = currentVelocity.add(velocityBoost);

            // Ensure the velocity does not exceed the maximum speed
            if (newVelocity.length() > MAX_SPEED) {
                newVelocity = newVelocity.normalize().multiply(MAX_SPEED); // Clamp to max speed
            }

            // Apply the new velocity to the player
            player.setVelocity(newVelocity);
            player.sendMessage("set velocity");
        }

        if (player.isSneaking()) {
            player.setGliding(false);
        }
    }
}