package net.redcocoa.dodgecube;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (BanDB.isBanned(event.getPlayer())) {
            // TODO ban message here
        }
    }
}
