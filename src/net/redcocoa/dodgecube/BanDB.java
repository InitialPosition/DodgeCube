package net.redcocoa.dodgecube;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BanDB {
    private static final Path BAN_DIRECTORY = Paths.get(System.getProperty("user.dir") + "/bans");
    private static final Logger logger = Logger.getLogger("BanDB");

    /**
     * Ensure the ban directory exists. If the plugin can't find one, it will create one.
     */
    public static void initBanDatabase() {
        // check file system and create folder if needed
        if (!Files.isDirectory(BAN_DIRECTORY)) {
            logger.log(Level.INFO, "[DodgeCube] No ban database found, creating new one...");
            try {
                Files.createDirectory(BAN_DIRECTORY);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "[DodgeCube] Error creating ban database! Do you have writing permissions?");
                System.exit(0);
            }
        } else {
            int file_count = Objects.requireNonNull(new File(String.valueOf(BAN_DIRECTORY)).list()).length;
            if (file_count != 1) {
                logger.log(Level.INFO, MessageFormat.format("[DodgeCube] {0} bans in database.", file_count));
            } else {
                logger.log(Level.INFO, "[DodgeCube] 1 ban in database.");
            }
        }
    }

    /**
     * Write a new ban to the database. Writes a file named after the banned players' user ID containing the banners'
     * user ID.
     *
     * @param banner The player that killed (and therefore banned) the other player.
     * @param banned The player that was killed (and therefore banned) by the other player.
     */
    public static void writeBan(Player banner, Player banned) {
        String banned_player_id = banned.getUniqueId().toString();
        String banner_player_id = banner.getUniqueId().toString();

        String file_path = MessageFormat.format("{0}/{1}", BAN_DIRECTORY, banned_player_id);

        File file = new File(file_path);
        try {
            if (file.createNewFile()) {
                try {
                    FileWriter writer = new FileWriter(file_path);
                    writer.write(banner_player_id);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "[DodgeCube] Could not write ban to disk!");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[DodgeCube] Could not write ban to disk!");
        }
    }

    /**
     * Checks whether a given player is currently banned by the plugin.
     * @param player The player to check the ban status for
     * @return true if the player is banned, false otherwise
     */
    public static boolean isBanned(Player player) {
        String uuid = player.getUniqueId().toString();
        File banfile = new File(MessageFormat.format("{0}/{1}", BAN_DIRECTORY, uuid));
        return banfile.exists() && !banfile.isDirectory();
    }
}
