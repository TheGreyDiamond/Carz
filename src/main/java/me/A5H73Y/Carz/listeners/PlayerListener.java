package me.A5H73Y.Carz.listeners;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.other.DelayTasks;
import me.A5H73Y.Carz.other.Utils;
import me.A5H73Y.Carz.other.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final Carz carz;

    public PlayerListener(Carz carz) {
        this.carz = carz;
    }

    @EventHandler
    public void onPlaceMinecart(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (Utils.getMaterialInPlayersHand(event.getPlayer()) != Material.MINECART)
            return;

        if (event.getClickedBlock().getType() == XMaterial.RAIL.parseMaterial()
                || event.getClickedBlock().getType() == XMaterial.POWERED_RAIL.parseMaterial()
                || event.getClickedBlock().getType() == XMaterial.DETECTOR_RAIL.parseMaterial())
            return;

        Player player = event.getPlayer();

        if (!Utils.hasPermission(player, Permissions.PLACE))
            return;

        if (!DelayTasks.getInstance().delayPlayer(player, 3))
            return;

        ItemStack carInHand = Utils.getItemStackInPlayersHand(player);

        if (carInHand.hasItemMeta() && carInHand.getItemMeta().hasDisplayName()) {
            if (carInHand.getItemMeta().getDisplayName().contains(player.getName())) {
                Utils.spawnOwnedCar(event.getClickedBlock().getLocation(), player);
            } else {
                player.sendMessage(Utils.getTranslation("Error.Owned"));
                return;
            }
        } else {
            Utils.spawnCar(event.getClickedBlock().getLocation());
        }

        Utils.reduceItemStackInPlayersHand(player);
    }
}
