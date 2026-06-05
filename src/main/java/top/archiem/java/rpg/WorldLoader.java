package top.archiem.java.rpg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.archiem.java.rpg.types.Enemy;
import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;
import top.archiem.java.rpg.types.Room;

public class WorldLoader{
    
    public static HashMap<String, Room> loadWorld(String fileName){
        List<String> lines = new ArrayList<>();
        HashMap<String, Room> rooms = new HashMap<>();

        //Empty constants for creating rooms
        HashMap<String, Room> emptyConnections = new HashMap<>();
        ArrayList<Item> emptyItems = new ArrayList<>();
        ArrayList<Enemy> emptyEnemies = new ArrayList<>();

        String startRoomName = null;

        // Read file once and store all lines
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                lines.add(line);
            }

        } catch (IOException e) {
            System.out.println("Failed to load world file: " + fileName);
            return null;
        }

        for(String line : lines){
            String[] parts = line.split(":");

            if(parts[0] == "ROOM"){

                Room newRoom = new Room(emptyEnemies, emptyItems, emptyConnections, parts[2], parts[1]);
                rooms.put(parts[1], newRoom);

            }
        }

        for(String line : lines){
            String[] parts = line.split(":");

            switch(parts[0]){
                case "ROOM" -> {}
                case "START" -> {
                    Room originRoom = rooms.get(parts[1]);
                    if(originRoom == null){
                        System.out.println("Warning: unknown room in ENEMY line - " + line);
                        break;
                    }
                    startRoomName = parts[1];
                    originRoom.setIsStart(true);
                }
                case "EXIT" -> {
                    Room originRoom = rooms.get(parts[1]);
                    String direction = parts[2];
                    Room destination = rooms.get(parts[3]);
                    if (originRoom == null || destination == null) {
                        System.out.println("Warning: unknown room in EXIT line - " + line);
                        break;
                    }
                    originRoom.addExit(direction, destination);
                }
                case "ENEMY" -> {
                    Room originRoom = rooms.get(parts[1]);
                    if(originRoom == null){
                        System.out.println("Warning: unknown room in ENEMY line - " + line);
                        break;
                    }
                    switch(parts[2]){
                        case "zombie" -> {
                            originRoom.addEnemy(Enemy.zombie());
                        }
                        case "ghost" -> {
                            originRoom.addEnemy(Enemy.ghost());
                        }
                        case "skeleton" -> {
                            originRoom.addEnemy(Enemy.skeleton());
                        }
                        case "dragon" -> {
                            originRoom.addEnemy(Enemy.dragon());
                        }
                        
                    }
                }
                case "ITEM" -> {
                    Room originRoom = rooms.get(parts[1]);
                    if(originRoom == null){
                        System.out.println("Warning: unknown room in ENEMY line - " + line);
                        break;
                    }
                    Item newItem = new Item(ItemTypes.valueOf(parts[3]), Integer.valueOf(parts[4]), parts[2], Integer.valueOf(parts[5]));
                    originRoom.addItem(newItem);
                }
                case "SHOP" -> {
                    Room originRoom = rooms.get(parts[1]);
                    if(originRoom == null){
                        System.out.println("Warning: unknown room in ENEMY line - " + line);
                        break;
                    }

                    originRoom.setIsShop(true);
                }
                case "BOSS" -> {
                    Room originRoom = rooms.get(parts[1]);
                    if(originRoom == null){
                        System.out.println("Warning: unknown room in ENEMY line - " + line);
                        break;
                    }
                    
                    originRoom.setIsBoss(true);
                }
            }
        }

        return rooms;
    }

}