package neotran.plugins.enotshop;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

@Getter
public class SoldItem {

    private ItemStack item;
    private int price;

    public SoldItem(Sign sign) {
        String[] split = sign.getLine(1).split(":");
        Material type = org.bukkit.Material.valueOf(split[0].toUpperCase());
        int data = 0;
        if (split.length > 1 && split[1].matches("[0-9]+")) {
            data = Integer.parseInt(split[1]);
        }
        int amount = Integer.parseInt(sign.getLine(2));
        item = new ItemStack(type, amount, (short) data);
        price = Integer.parseInt(sign.getLine(3));
    }

    public Material getType() {
        return item.getType();
    }

    public MaterialData getData() {
        return item.getData();
    }

    public int getAmount() {
        return item.getAmount();
    }
}
