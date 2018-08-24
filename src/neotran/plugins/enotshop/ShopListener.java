package neotran.plugins.enotshop;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ShopListener implements Listener {

    @EventHandler
    void onBlockBreakSign(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.SIGN_POST || e.getBlock().getType() == Material.WALL_SIGN) {
            ShopOwnerStorage.removeOwner(e.getBlock());
        }
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (e.getClickedBlock() == null || !(e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN)) {
            return;
        }

        if (!ShopOwnerStorage.containsOwner(e.getClickedBlock())) {
            return;
        }

        Sign sign = (Sign) e.getClickedBlock().getState();

        if (!isValidShopSign(sign.getLines(), e.getPlayer())) {
            return;
        }

        Set<Chest> chests = findChests(e.getClickedBlock());
        if (chests.size() < 1) {
            e.getPlayer().sendMessage("§cНе найден сундук рядом с табличкой");
            return;
        }

        SoldItem soldItem = new SoldItem(sign);

        if (!VaultEconomy.has(e.getPlayer(), soldItem.getPrice())) {
            e.getPlayer().sendMessage("§cНедостаточно средств");
            e.setCancelled(true);
            return;
        }

        for (Chest chest : chests) {
            for (ItemStack itemStack : chest.getInventory().getContents())  {
                if (itemStack == null || itemStack.getType() != soldItem.getType() || !soldItem.getData().equals(itemStack.getData())) {
                    continue;
                }

                if (itemStack.getAmount() < soldItem.getAmount()) {
                    continue;
                }

                chest.getInventory().removeItem(itemStack);
                ItemStack newAmountItem = reduceAmount(itemStack, soldItem.getAmount());

                if (newAmountItem != null) {
                    chest.getInventory().addItem(newAmountItem);
                }

                e.getPlayer().getInventory().addItem(soldItem.getItem());
                VaultEconomy.take(e.getPlayer(), soldItem.getPrice());
                VaultEconomy.add(ShopOwnerStorage.getOwner(e.getClickedBlock()), soldItem.getPrice());

                e.getPlayer().sendMessage("§eУспешно!");

                return;
            }
        }

        e.getPlayer().sendMessage("§cТовар закончился");
    }

    @EventHandler
    void onSignChange(SignChangeEvent e) {
        if (!"[shop]".equals(e.getLine(0).toLowerCase())) {
            return;
        }

        if (!e.getPlayer().hasPermission(Main.getInstance().getConfig().getString("permission"))) {
            e.getPlayer().sendMessage("§cНедостаточно прав для создания магазина");
            return;
        }

        if (findChests(e.getBlock()).size() < 1) {
            e.getPlayer().sendMessage("§cНеобходимо поставить сундук рядом с табличкой");
            e.setCancelled(true);
            return;
        }

        if (!isValidShopSign(e.getLines(), e.getPlayer())) {
            return;
        }

        ShopOwnerStorage.addOwner(e.getPlayer().getName(), e.getBlock());
        e.getPlayer().sendMessage("§eМагазин установлен!");
    }

    private static ItemStack reduceAmount(ItemStack item, int amount) {
        int newAmount = item.getAmount() - amount;
        if (newAmount < 1) {
            return null;
        } else {
            item.setAmount(newAmount);
            return item;
        }
    }

    private static Set<Chest> findChests(Block sign) {
        Set<Chest> blockSet = new HashSet<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    Block block = sign.getRelative(x, y, z);
                    if (block.getType() == Material.CHEST) {
                        blockSet.add(((Chest) block.getState()));
                    }
                }
            }
        }

        return blockSet;
    }

    private static boolean isValidShopSign(String[] lines, Player player) {
        if (lines[0] == null || lines[1] == null || lines[2] == null || lines[3] == null) {
            player.sendMessage("§cОсталась пустая строка. Не удалось создать магазин.");
            return false;
        }

        Material material = null;

        String[] split = lines[1].split(":");

        try {
            material = Material.valueOf(split[0].toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        if (material == null) {
            player.sendMessage("§cНекорректное название предмета. Не удалось создать магазин.");
            return false;
        }

        if (!lines[2].matches("[0-9]+") || !lines[3].matches("[0-9]+")) {
            player.sendMessage("§cНекорректное количество товара или цена. Не удалось создать магазин.");
            return false;
        }

        int amount = Integer.parseInt(lines[2]);

        if (amount < 0 || amount > 64) {
            player.sendMessage("§cКоличество товара должно быть в диапазоне 1-64. Не удалось создать магазин.");
            return false;
        }

        return true;
    }
}
