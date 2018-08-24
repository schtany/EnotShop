package neotran.plugins.enotshop;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new ShopListener(), instance);

        ShopOwnerStorage.loadOwners(new File(getDataFolder(), "shops.txt"));
    }

    @Override
    public void onDisable() {
        ShopOwnerStorage.saveOwners(new File(getDataFolder(), "shops.txt"));
    }

}
