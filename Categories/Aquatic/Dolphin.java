
package Blink.project.Abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class Dolphin implements Listener {

    private final JavaPlugin plugin;

    public Dolphin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<String> allowedPlayers = Arrays.asList("Goannas");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.dolphin", true);
        }

        if (!player.hasPermission("auro.dolphin")) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 1, false, false, false));
    }
}
