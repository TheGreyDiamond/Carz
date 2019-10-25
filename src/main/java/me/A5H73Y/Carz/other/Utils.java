package me.A5H73Y.Carz.other;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import com.connorlinfoot.bountifulapi.BountifulAPI;
import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Commands;
import me.A5H73Y.Carz.enums.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    /**
     * Get translation of string key.
     * The string parameter will be matched to an entry in the Strings.yml
     * The boolean will determine whether to display the Carz prefix
     *
     * @param translationKey to translate
     * @param prefix display Parkour prefix
     * @return String of appropriate translation
     */
    public static String getTranslation(String translationKey, boolean prefix) {
        if (!Validation.isStringValid(translationKey)) {
            return "Invalid translation.";
        }

        String translated = Carz.getInstance().getSettings().getStringsConfig().getString("Message." + translationKey);
        translated = translated != null ? colour(translated) : "String not found: " + translationKey;
        return prefix ? Carz.getPrefix().concat(translated) : translated;
    }

    /**
     * Override method, but with a default of an enabled Carz prefix.
     *
     * @param translationKey to translate
     * @return String of appropriate translation
     */
    public static String getTranslation(String translationKey) {
        return getTranslation(translationKey, true);
    }


    /**
     * Check if the player has the specified permission.
     * This will return true if permissions are disabled
     * @param player
     * @param permission
     * @return boolean
     */
    public static boolean hasPermission(Player player, Permissions permission) {
        if (!Carz.getInstance().getConfig().getBoolean("Other.UsePermissions"))
            return true;

        return hasStrictPermission(player, permission);
    }

    /**
     * Check if the player has the specified permission.
     * The player will be sent a message if they don't have the permission.
     * @param player
     * @param permission
     * @return hasPermission
     */
    public static boolean hasStrictPermission(Player player, Permissions permission) {
        return hasStrictPermission(player, permission, true);
    }

    /**
     * Check if the player has the specified permission.
     * This will strictly check if the player has permission / op.
     * @param player
     * @param permission
     * @param displayMessage
     * @return hasPermission
     */
    public static boolean hasStrictPermission(Player player, Permissions permission, boolean displayMessage) {
        if (player.hasPermission(permission.getPermission())
                || player.hasPermission(Permissions.ALL.getPermission())
                || player.isOp())
            return true;

        if (displayMessage) {
            player.sendMessage(Utils.getTranslation("Error.NoPermission")
                    .replace("%PERMISSION%", permission.getPermission()));
        }
        return false;
    }

    /**
     * Destroy all Minecarts on the server.
     */
    public static void destroyAllCars() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Minecart) {
                    entity.remove();
                }
            }
        }
    }

    /**
     * Spawn a vehicle at the given location.
     * If a player is provided, they will be declared the owner
     * @param location
     * @param player
     */
    public static void spawnOwnedCar(Location location, Player player) {
        location.add(0, 1, 0);
        Minecart spawnedCar = location.getWorld().spawn(location, Minecart.class);
//        TODO make configurable
//        BlockData data = Bukkit.createBlockData(Material.WET_SPONGE);
//        spawnedCar.setDisplayBlockData(data);

        if (player != null) {
            Carz.getInstance().getCarController().declareOwnership(spawnedCar.getEntityId(), player.getName());
        }
    }

    /**
     * Spawn an owner-less vehicle at the given location.
     * @param location
     */
    public static void spawnCar(Location location) {
        spawnOwnedCar(location, null);
    }

    /**
     * Place an Minecart in the player's inventory with their name on it.
     * @param player
     */
    public static void givePlayerOwnedCar(Player player) {
        ItemStack s = new ItemStack(Material.MINECART);
        ItemMeta m = s.getItemMeta();
        m.setDisplayName(Utils.getTranslation("PlayerCar", false).replace("%PLAYER%", player.getName()));
        s.setItemMeta(m);
        player.getInventory().addItem(s);

        player.updateInventory();
    }

    /**
     * Translate colour codes of provided message.
     * @param message
     * @return string
     */
    public static String colour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Format and standardize text to a constant case.
     * Will transform "hElLO" into "Hello"
     *
     * @param text
     * @return standardized input
     */
    public static String standardizeText(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
    }

    /**
     * Return the standardised heading used for Carz
     * @param headingText
     * @return standardised Carz heading
     */
    public static String getStandardHeading(String headingText){
        return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
    }

    /**
     * Check if the argument is numeric.
     * "1" - true, "Hi" - false
     *
     * @param text
     * @return whether the input is numeric
     */
    public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Check to see if a command is disabled from the config
     * This forces the players to use signs instead
     * @param player
     * @param command
     * @return boolean
     */
    public static boolean commandEnabled(Player player, Commands command) {
        boolean enabled = Carz.getInstance().getConfig().getBoolean(command.getConfigPath());

        if (!enabled) {
            player.sendMessage(Utils.getTranslation("Error.CommandDisabled"));
        }

        return enabled;
    }

    /**
     * Made because < 1.8
     * @param player
     * @return ItemStack
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemStackInPlayersHand(Player player) {
        ItemStack stack;

        try {
            stack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ex) {
            stack = player.getItemInHand();
        }

        return stack;
    }

    /**
     * Get the Material in the player's hand.
     * @param player
     * @return Material
     */
    public static Material getMaterialInPlayersHand(Player player) {
        return getItemStackInPlayersHand(player).getType();
    }

    /**
     * Reduce the number of item in hand by 1.
     * @param player
     */
    public static void reduceItemStackInPlayersHand(Player player) {
        getItemStackInPlayersHand(player).setAmount(getItemStackInPlayersHand(player).getAmount() - 1);
    }

    /**
     * Lookup the matching Material.
     * Use the 1.13 API to lookup the Material,
     * It will fall back to XMaterial if it fails to find it
     * @param materialName
     * @return matching Material
     */
    public static Material lookupMaterial(String materialName) {
        materialName = materialName.toUpperCase();
        Material material = Material.getMaterial(materialName);

        if (material == null) {
            XMaterial lookup = XMaterial.fromString(materialName);

            if (lookup != null) {
                material = lookup.parseMaterial();
            }
        }

        return material;
    }

    /**
     * Used for logging plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe.
     *
     * @param message
     * @param severity (0 - 2)
     */
    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Carz.getInstance().getLogger().warning(message);
                break;
            case 2:
                Carz.getInstance().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                Carz.getInstance().getLogger().info(message);
                break;
        }
    }

    /**
     * Convert a list of material names to a unique set of Materials.
     * @param rawMaterials
     * @return Set<Material>
     */
    public static Set<Material> convertToValidMaterials(List<String> rawMaterials) {
        Set<Material> validMaterials = new HashSet<>();

        for (String rawMaterial : rawMaterials) {
            Material material = Utils.lookupMaterial(rawMaterial);
            if (material != null) {
                validMaterials.add(material);
            } else {
                Utils.log("Material '" + rawMaterial + "' is invalid", 2);
            }
        }
        return validMaterials;
    }

    /**
     * Add a ClimbBlock to the list
     * @param player
     * @param args
     */
    public static void addClimbBlock(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Carz.getPrefix() + "Invalid syntax: /ns addcb (material)");
            return;
        }

        Material material = Utils.lookupMaterial(args[1]);

        if (material == null) {
            player.sendMessage(Carz.getPrefix() + args[1] + " is not a valid Material!");
            return;
        }

        Carz.getInstance().getSettings().addClimbBlock(material);
        player.sendMessage(Carz.getPrefix() + material.name() + " added to ClimbBlocks!");
    }

    /*public static void sendTitle(Player player, String message) {
        if (Carz.getInstance().getSettings().isUsingBountiful()) {
            BountifulAPI.sendTitle(player, 5, 20, 5, message, null);
        } else {
            player.sendMessage(Carz.getPrefix() + message);
        }
    }*/

    /*public static void sendActionBar(Player player, String message) {
        if (Carz.getInstance().getSettings().isUsingBountiful()) {
            BountifulAPI.sendActionBar(player, message, 20);
        } else {
            player.sendMessage(Carz.getPrefix() + message);
        }
    }*/
}
