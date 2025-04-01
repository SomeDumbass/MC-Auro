import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class IronGolem implements Listener {

    private final JavaPlugin plugin;
    private final List<String> allowedPlayers = Arrays.asList("Goannas");
    private final Set<Location> villageCenters = new HashSet<>();

    public IronGolem(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeVillageCenters();
    }

    private void initializeVillageCenters() {
        // Add known village center locations
        // Example:
        villageCenters.add(new Location(Bukkit.getWorld("world"), 100, 70, 200));
        villageCenters.add(new Location(Bukkit.getWorld("world"), -150, 65, 300));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (allowedPlayers.contains(player.getName())) {
            player.addAttachment(plugin, "auro.irongolem", true);
            // Set armor attribute
            AttributeInstance armorAttribute = player.getAttribute(Attribute.GENERIC_ARMOR);
            if (armorAttribute != null) {
                armorAttribute.setBaseValue(10.0); // Increase armor value
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (allowedPlayers.contains(player.getName()) && player.hasPermission("auro.irongolem")) {
                if (isInVillage(player.getLocation())) {
                    // Increase damage in village
                    event.setDamage(event.getDamage() * 1.5); // Increase damage by 50%
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (allowedPlayers.contains(player.getName()) && player.hasPermission("auro.irongolem")) {
                if (isInVillage(player.getLocation())) {
                    // Add health boost in village
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 2, false, false));
                }
            }
        }
    }

    private boolean isInVillage(Location location) {
        for (Location villageCenter : villageCenters) {
            if (location.getWorld().equals(villageCenter.getWorld())) {
                double distance = location.distance(villageCenter);
                if (distance <= 32) { // Adjust the distance as needed
                    return true;
                }
            }
        }
        return false;
    }
}
