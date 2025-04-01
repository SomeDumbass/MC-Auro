package Blink.project;

import Blink.project.Abilities.*;
import Blink.project.AbilitiesNew.TridentCraftingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Project extends JavaPlugin {

    @Override
    public void onEnable() {
//        Bukkit.getServer().getPluginManager().registerEvents(new Aquatic(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Arachnid(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Avian(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Blaze(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Creeper(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Enderian(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Feline(), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Piglin(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Shulker(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Slime(this), this);
//        Bukkit.getServer().getPluginManager().registerEvents(new Wither(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
