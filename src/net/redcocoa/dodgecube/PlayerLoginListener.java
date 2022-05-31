package net.redcocoa.dodgecube;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.util.logging.Level;

public class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.isBanned()) {
            if (BanDB.isBanned(player)) {
                File banFile = new File(BanDB.BAN_DIRECTORY_STRING + "/" + player.getUniqueId());

                if (banFile.delete()) {
                    DodgeCube.LOGGER.log(Level.INFO, "[DodgeCube] Deleted remaining ban file for user");
                }
            }
        }
    }
}
