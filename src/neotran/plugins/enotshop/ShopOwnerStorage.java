package neotran.plugins.enotshop;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import saharnooby.lib.json.ExtraObject;
import saharnooby.lib.json.LibJson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShopOwnerStorage {

    private static Map<Block, String> shops = new HashMap<>();

    public static boolean contains(@NonNull Block sign) {
        return shops.containsKey(sign);
    }

    public static String getOwner(@NonNull Block sign) {
        return shops.get(sign);
    }

    public static void addShop(@NonNull String owner, Block sign) {
        shops.put(sign, owner);
    }

    public static void removeShop(@NonNull Block sign) {
        if (shops.containsKey(sign)) {
            System.out.println("[EnotShop] " + shops.get(sign) + "'s shop has been removed");
            shops.remove(sign);
        }
    }

    public static void saveShops(@NonNull File file) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            for (Map.Entry<Block, String> shop : shops.entrySet()) {

                String signBlock = shop.getKey().getX() + "/" + shop.getKey().getY() + "/" + shop.getKey().getZ() + "/" + shop.getKey().getWorld().getName();

                ExtraObject data = ExtraObject.of("owner", shop.getValue(), "sign", signBlock);

                String s = data.toString() + "\r\n";

                fileWriter.write(s);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadShops(@NonNull File file) {
        try {
            if (!file.exists()) {
                System.out.println("Shops file not found");
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            bufferedReader.lines().forEach(s->{
                Object o = LibJson.parseJson(s);
                ExtraObject object = (ExtraObject) o;

                String owner = object.getString("owner");

                String loc = object.getString("sign");
                String[] split = loc.split("/");
                Block sign = Bukkit.getWorld(split[3])
                        .getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

                shops.put(sign, owner);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
