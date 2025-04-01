package Blink.project.Abilities;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Aquatic implements Listener {

    private final JavaPlugin plugin;

    public Aquatic(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Add an array of player names
    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.aquatic", true);
        }

        if (player.hasPermission("auro.aquatic")) {
            setUnderwaterAttributes(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.aquatic")) {
            if (player.isInWater() && !player.isSneaking()) {
                setNoGravity(player, true);
            } else {
                setNoGravity(player, false);
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auro.aquatic") && player.isInWater()) {
            setNoGravity(player, !event.isSneaking());
        }
    }

    private void setNoGravity(Player player, boolean noGravity) {
        player.setGravity(!noGravity); // Using setGravity to control the gravity effect
    }

    private void setUnderwaterAttributes(Player player) {
        // Increase underwater movement speed
        AttributeInstance waterMovementEfficiency = player.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);
        if (waterMovementEfficiency != null) {
            waterMovementEfficiency.setBaseValue(0.15); // Adjust the value as needed
        }

        // Increase underwater block breaking speed
        AttributeInstance submergedMiningSpeed = player.getAttribute(Attribute.SUBMERGED_MINING_SPEED);
        if (submergedMiningSpeed != null) {
            submergedMiningSpeed.setBaseValue(10); // Adjust the value as needed
        }

        // Set the oxygen bonus to maximum integer value
        AttributeInstance oxygenBonus = player.getAttribute(Attribute.OXYGEN_BONUS);
        if (oxygenBonus != null) {
            oxygenBonus.setBaseValue(Integer.MAX_VALUE); // Set to maximum int value
        }

        // Increase fishing luck
        AttributeInstance luck = player.getAttribute(Attribute.LUCK);
        if (luck != null) {
            luck.setBaseValue(10); // Adjust the value as needed
        }
    }
}
