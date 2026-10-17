package top.archiem.java.rpg;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.archiem.java.rpg.errors.SaveLoadException;
import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.helpers.ItemParser;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;
import top.archiem.java.rpg.types.Save;
import top.archiem.java.rpg.types.SaveTypes;
import top.archiem.java.rpg.types.ShopItem;
import java.nio.file.Files;

public class SaveManager {

    File saveFile;

    public void initSaves() {
        try {
            saveFile = new File("save.txt");
            saveFile.createNewFile();
        } catch (Exception e) {
            try {
                FileWriter tempWriter = new FileWriter("save.txt");
                saveFile = new File("save.txt");
            } catch (IOException ex) {
                System.out.println(AnsiColors.red("Failed all attempts to load save file."));
            }
        }
    }

    public void save(HashMap<String, Room> rooms, Player player, Room currentRoom, ArrayList<ShopItem> shopItems) {
        Item equippedArmour = player.getEquippedArmour();
        Item equippedWeapon = player.getEquippedWeapon();
        if (saveFile == null) {
            System.out.println(AnsiColors.red("Save system not initialised"));
            return;
        }
        System.out.println(AnsiColors.red("Saving... Do not close the terminal!"));
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile), true)) {
            int weaponBonus = equippedWeapon == null ? 0 : equippedWeapon.getValue();
            int armourBonus = equippedArmour == null ? 0 : equippedArmour.getValue();

            writer.println(SaveTypes.NAME.toString() + ":" + player.getName());
            writer.println(SaveTypes.XP.toString() + ":" + player.getXp());
            writer.println(SaveTypes.LEVEL.toString() + ":" + player.getLevel());
            writer.println(SaveTypes.HP.toString() + ":" + player.getHp() + ":" + player.getMaxHp());
            writer.println(SaveTypes.ATTACK.toString() + ":" + (player.getAttack() - weaponBonus));
            writer.println(SaveTypes.DEFENSE.toString() + ":" + (player.getDefence() - armourBonus));
            writer.println(SaveTypes.GOLD.toString() + ":" + player.getGold());
            if (equippedWeapon != null) {
                writer.println(SaveTypes.WEAPON.toString() + ":" + ItemParser.serialize(equippedWeapon));
            }
            if (equippedArmour != null) {
                writer.println(SaveTypes.ARMOUR.toString() + ":" + ItemParser.serialize(equippedArmour));
            }
            for (Item i : player.getInventory()) {
                writer.println(SaveTypes.ITEM.toString() + ":" + ItemParser.serialize(i));
            }
            for (Room r : rooms.values()) {
                if (!r.hasAliveEnemies()) {
                    writer.println(SaveTypes.CLEARED.toString() + ":" + r.getName());
                }
                if (r.getItems().size() > 0) {
                    for (Item i : r.getItems()) {
                        writer.println(SaveTypes.PICKUP.toString() + ":" + r.getName() + ":" + ItemParser.serialize(i));
                    }
                }
            }
            for (ShopItem i : shopItems) {
                writer.println(SaveTypes.SHOPITEM.toString() + ":" + ItemParser.serialize(i) + ":" + i.getCost());
            }
            writer.println(SaveTypes.CURRENT.toString() + ":" + currentRoom.getName());
            System.out.println(AnsiColors.green("Saved successfully to " + saveFile.getCanonicalPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Save load(HashMap<String, Room> roomList) {
        Save save = new Save();
        save.setPlayer(new Player(""));
        Player savePlayer = save.getPlayer();
        save.setUpdatedRooms(roomList);
        save.setShopItems(new ArrayList<ShopItem>());
        HashMap<String, Room> rooms = save.getUpdatedRooms();
        Room currentRoom = save.getCurrentRoom();

        if (saveFile == null) {
            System.out.println("Save system not initialised");
            return null;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(saveFile.toPath());
        } catch (IOException e) {
            throw new SaveLoadException("Could not read save file: " + e.getMessage(), true);
        }

        if (lines.isEmpty()) {
            System.out.println("Save file is empty, nothing to load");
            return null;
        }

        for (int idx = 0; idx < lines.size(); idx++) {
            int lineNo = idx + 1;
            String line = lines.get(idx);
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(":");
            if (parts.length == 0) {
                throw new SaveLoadException("Malformed entry on line " + lineNo, true);
            }

            SaveTypes type;
            try {
                type = SaveTypes.valueOf(parts[0].trim());
            } catch (IllegalArgumentException e) {
                throw new SaveLoadException("Unknown save entry '" + parts[0] + "' on line " + lineNo, true);
            }

            try {
                switch (type) {
                    case NAME -> savePlayer.setName(stringPart(parts, 1, "Name", lineNo));
                    case XP -> savePlayer.setXp(intPart(parts, 1, "XP", lineNo));
                    case HP -> savePlayer.setHp(intPart(parts, 1, "HP", lineNo));
                    case DEFENSE -> savePlayer.setDefence(intPart(parts, 1, "Defense", lineNo));
                    case GOLD -> savePlayer.setGold(intPart(parts, 1, "Gold", lineNo));
                    case LEVEL -> savePlayer.setLevel(intPart(parts, 1, "Level", lineNo));
                    case ATTACK -> savePlayer.setAttack(intPart(parts, 1, "Attack", lineNo));
                    case PICKUP -> {
                        Room room = roomPart(rooms, parts, 1, lineNo);
                        room.addItem(itemPart(parts, 2, lineNo));
                    }
                    case ITEM -> savePlayer.getInventory().add(itemPart(parts, 1, lineNo));
                    case ARMOUR -> savePlayer.equipArmour(itemPart(parts, 1, lineNo));
                    case WEAPON -> savePlayer.equipWeapon(itemPart(parts, 1, lineNo));
                    case SHOPITEM -> {
                        Item toAdd = itemPart(parts, 1, lineNo);
                        int cost = intPart(parts, 5, "Shop item cost", lineNo);
                        save.getShopItems().add(new ShopItem(toAdd.getItemType(), toAdd.getValue(), toAdd.getName(), toAdd.getGoldValue(), cost));
                    }
                    case CLEARED -> {
                        Room room = roomPart(rooms, parts, 1, lineNo);
                        room.getEnemies().clear();
                    }
                    case CURRENT -> save.setCurrentRoom(roomPart(rooms, parts, 1, lineNo));
                    default -> { /* blank */ }
                }
            } catch (SaveLoadException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new SaveLoadException("Unexpected error on line " + lineNo + " (" + type + "): " + e, true);
            }
        }

        return save;
    }

    // ---- helpers ----

    private static String stringPart(String[] parts, int index, String label, int lineNo) {
        if (index >= parts.length || parts[index].isBlank()) {
            throw new SaveLoadException(label + " is missing on line " + lineNo, true);
        }
        return parts[index];
    }

    private static int intPart(String[] parts, int index, String label, int lineNo) {
        String raw = stringPart(parts, index, label, lineNo);
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new SaveLoadException(label + " is not an integer ('" + raw + "') on line " + lineNo, true);
        }
    }

    private static Item itemPart(String[] parts, int start, int lineNo) {
        try {
            return ItemParser.parseViaParts(parts, start);
        } catch (IllegalArgumentException e) {
            throw new SaveLoadException("Invalid item on line " + lineNo + ": " + e.getMessage(), true);
        }
    }

    private static Room roomPart(HashMap<String, Room> rooms, String[] parts, int index, int lineNo) {
        String roomName = stringPart(parts, index, "Room name", lineNo);
        Room room = rooms.get(roomName);
        if (room == null) {
            throw new SaveLoadException("Unknown room '" + roomName + "' on line " + lineNo, true);
        }
        return room;
    }

}