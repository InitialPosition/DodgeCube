package net.redcocoa.dodgecube;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class BanDB {
    public static final String BAN_DIRECTORY_STRING = System.getProperty("user.dir") + "/bans";
    private static final Path BAN_DIRECTORY = Paths.get(BAN_DIRECTORY_STRING);

    /**
     * Ensure the ban directory exists. If the plugin can't find one, it will create one.
     */
    public static void initBanDatabase() {
        // check file system and create folder if needed
        if (!Files.isDirectory(BAN_DIRECTORY)) {
            DodgeCube.LOGGER.log(Level.INFO, "[DodgeCube] No ban database found, creating new one...");
            try {
                Files.createDirectory(BAN_DIRECTORY);
            } catch (IOException e) {
                DodgeCube.LOGGER.log(Level.SEVERE, "[DodgeCube] Error creating ban database! Do you have writing permissions?");
                System.exit(0);
            }
        } else {
            int file_count = Objects.requireNonNull(new File(String.valueOf(BAN_DIRECTORY)).list()).length;
            if (file_count != 1) {
                DodgeCube.LOGGER.log(Level.INFO, MessageFormat.format("[DodgeCube] {0} bans in database.", file_count));
            } else {
                DodgeCube.LOGGER.log(Level.INFO, "[DodgeCube] 1 ban in database.");
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

                    Calendar cl = Calendar.getInstance();
                    cl.add(Calendar.HOUR, 72);

                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(banned.getDisplayName(), MessageFormat.format(ChatColor.RED + "Died to {0}!" + ChatColor.WHITE, banner.getDisplayName()), cl.getTime(), null);
                } catch (IOException e) {
                    DodgeCube.LOGGER.log(Level.SEVERE, "[DodgeCube] Could not write ban to disk!");
                }
            }
        } catch (IOException e) {
            DodgeCube.LOGGER.log(Level.SEVERE, "[DodgeCube] Could not write ban to disk!");
        }
    }

    /**
     * Checks whether a given player is currently banned by the plugin.
     *
     * @param player The player to check the ban status for
     * @return true if the player is banned, false otherwise
     */
    public static boolean isBanned(Player player) {
        String uuid = player.getUniqueId().toString();
        System.out.println(MessageFormat.format("{0}/{1}", BAN_DIRECTORY, uuid));
        File banFile = new File(MessageFormat.format("{0}/{1}", BAN_DIRECTORY, uuid));
        return banFile.exists() && !banFile.isDirectory();
    }

    /**
     * Reads and returns the player data for a given ban file.
     *
     * @param filePath The ban file to check
     * @return The UUID of the player that banned the file's player
     */
    public static String readFileContent(String filePath) {
        File file = new File(filePath);
        try {
            Scanner reader = new Scanner(file);
            String bannerID = reader.nextLine();
            reader.close();

            return bannerID;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unban and delete all players with ban files for the given player.
     *
     * @param player The player whose ban kills should get unbanned.
     */
    public static void unbanAllPlayersBannedByPlayer(Player player) {
        // get all ban files
        File banDir = new File(BAN_DIRECTORY_STRING);
        String[] fileList = banDir.list();
        ArrayList<String> filesToDelete = new ArrayList<>();
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);

        if (fileList != null) {
            for (String s : fileList) {
                String info = readFileContent(BAN_DIRECTORY_STRING + "/" + s);
                if (Objects.equals(info, player.getUniqueId().toString())) {
                    filesToDelete.add(BAN_DIRECTORY_STRING + "/" + s);
                }

                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(s));
                if (offlinePlayer.isBanned()) {
                    banList.pardon(Objects.requireNonNull(offlinePlayer.getName()));
                }
            }
        }

        int deletionCounter = 0;
        for (String s : filesToDelete) {
            File file = new File(s);
            if (file.isDirectory()) {
                continue;
            }

            if (file.delete()) {
                deletionCounter++;
            } else {
                DodgeCube.LOGGER.log(Level.SEVERE, MessageFormat.format("[DodgeCube] Could not delete {0}!", s));
            }
        }

        DodgeCube.LOGGER.log(Level.INFO, MessageFormat.format("[DodgeCube] {0} players unbanned!", deletionCounter));
    }
}
