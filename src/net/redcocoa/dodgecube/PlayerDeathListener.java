package net.redcocoa.dodgecube;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.text.MessageFormat;

public class PlayerDeathListener implements Listener {
    /**
     * Checks for players that die and bans them if they were killed by another player.
     *
     * @param event The EntityDeathEvent to check for
     */
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        // make sure it's a player that died (and unban everyone killed by them if it was)
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player dead_player = (Player) event.getEntity();
        BanDB.unbanAllPlayersBannedByPlayer(dead_player);

        // also make sure the damage was by another entity
        if (!(dead_player.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) dead_player.getLastDamageCause();
        // also also make sure the player died from another player
        if (!(entityDamageByEntityEvent.getDamager() instanceof Player)) {
            return;
        }
        Player killing_player = (Player) entityDamageByEntityEvent.getDamager();
        BanDB.writeBan(killing_player, dead_player);

        dead_player.getInventory().clear();
        dead_player.kickPlayer(MessageFormat.format(ChatColor.RED + "Died to {0}!" + ChatColor.WHITE, killing_player.getDisplayName()));
    }
}
