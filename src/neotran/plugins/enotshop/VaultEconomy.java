package neotran.plugins.enotshop;

import lombok.NonNull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class VaultEconomy {
	
	private static Object economy;
	
	private VaultEconomy() {
	}
	
	private static void checkEnabled() {
		if (economy == null) {
			if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
				System.out.println("vault != null");
				
				System.out.println(Bukkit.getServicesManager().getKnownServices());
				
				if (Bukkit.getServicesManager().getRegistration(Economy.class) != null) {
					System.out.println("Economy is registered");
					
					economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
				}
			}
		}
		
		if (economy == null) {
			throw new IllegalStateException("Vault plugin not found");
		}
	}
	
	public static double get(@NonNull OfflinePlayer player) {
		checkEnabled();
		return ((Economy) economy).getBalance(player);
	}
	
	public static boolean has(@NonNull OfflinePlayer player, double amount) {
		checkEnabled();
		return ((Economy) economy).has(player, amount);
	}
	
	public static void add(@NonNull OfflinePlayer player, double amount) {
		checkEnabled();
		((Economy) economy).depositPlayer(player, amount);
	}
	
	public static void take(@NonNull OfflinePlayer player, double amount) {
		checkEnabled();
		((Economy) economy).withdrawPlayer(player, amount);
	}
	
	public static double get(@NonNull String player) {
		return get(Bukkit.getOfflinePlayer(player));
	}
	
	public static boolean has(@NonNull String player, double amount) {
		return has(Bukkit.getOfflinePlayer(player), amount);
	}
	
	public static void add(@NonNull String player, double amount) {
		add(Bukkit.getOfflinePlayer(player), amount);
	}
	
	public static void take(@NonNull String player, double amount) {
		take(Bukkit.getOfflinePlayer(player), amount);
	}
	
}
