package Blink.project.Categories.Aquatic;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dolphin implements Listener {

    private final JavaPlugin plugin;
    private final List<String> allowedPlayers = Arrays.asList("Goannas");
    private final List<Player> freezingPlayers = new ArrayList<>();
    private final Map<Player, BukkitTask> freezeTasks = new HashMap<>();

    public Dolphin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Grant permission to allowed players
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.dolphin", true);
        }

        // Apply Dolphin's Grace potion effect if the player has the permission
        if (hasDolphinPermission(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 1, false, false, false));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!hasDolphinPermission(player)) {
            return;
        }

        Location location = player.getLocation();
        World world = location.getWorld();

        if (world == null) {
            return; // Safeguard against null worlds
        }

        // Check the biome at the player's feet
        Biome biome = world.getBiome(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());

        if (biome == Biome.FROZEN_OCEAN || biome == Biome.COLD_OCEAN) {
            if (!freezingPlayers.contains(player)) {
                freezingPlayers.add(player);
                startFreezing(player);
            }
        } else {
            if (freezingPlayers.contains(player)) {
                freezingPlayers.remove(player);
                stopFreezing(player);
            }
        }
    }

    private void startFreezing(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (freezingPlayers.contains(player)) {
                player.setFreezeTicks(Integer.MAX_VALUE);
            }
        }, 0L, 20L); // Repeat every 7 seconds

        freezeTasks.put(player, task); // Store the task for later cancellation
    }

    private void stopFreezing(Player player) {
        BukkitTask task = freezeTasks.remove(player);

        //Cancel running task
        if (task != null) {
            task.cancel();
        }
    }

    private boolean hasDolphinPermission(Player player) {
        return player.hasPermission("auro.dolphin");
    }

    @EventHandler
    public void onFishConsumed(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!hasDolphinPermission(player)) return;
    
        ItemStack item = event.getItem();
        Material material = item.getType();
        if (isFish(material)) {
            int baseHunger = getBaseHunger(material);
            int bonusHunger = 2;
            int newHunger = Math.min(player.getFoodLevel() + baseHunger + bonusHunger, 20);
            player.setFoodLevel(newHunger);
        }
    }

    private boolean isFish(Material material) {
        return material == Material.COD || material == Material.COOKED_COD
            || material == Material.SALMON || material == Material.COOKED_SALMON
            || material == Material.TROPICAL_FISH 
            || material == Material.PUFFERFISH;
    }

    private int getBaseHunger(Material material) {
        return switch (material) {
            case COD, TROPICAL_FISH, PUFFERFISH -> 1;
            case COOKED_COD -> 5;
            case SALMON -> 2;
            case COOKED_SALMON -> 6;
            default -> 0;
        };
    }
}
