package top.archiem.java.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;

public class GameEngine {
    Player player;
    boolean running = false;
    HashMap<String, Room> rooms;
    Room currentRoom;
    String command;


    public void startGameLoop() {
        running = true;

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Loading map...");
        rooms = WorldLoader.loadWorld("world.txt");
        System.out.println(rooms.toString());
        currentRoom = rooms.get(WorldLoader.getStartRoom());
        System.out.println(WorldLoader.getStartRoom().toString());
        System.out.println("Welcome to jaRpg, an interactive dungeon crawler game");
        System.out.println("To start, create a name for your player: ");
        String name = keyboard.nextLine().trim();
        player = new Player(name);
        System.out.println("Nice to meet you " + name + ". Let's start your adventure");
        System.out.println("You are just entered a dungeon ");
        currentRoom.describe();
        System.out.println("What do you want to do (type help if you need it)");
        String command = keyboard.nextLine().trim();
        parseCommand(command);
        while(running){
            System.out.println("Take an action: ");
            command = keyboard.nextLine().trim();
            parseCommand(command);
        }


    }

    private void parseCommand(String input){
        String[] part = input.split(" ",2);

        switch(part[0].toLowerCase()){
            case "go" -> {
                String direction = part[1];
                if(!currentRoom.connectionExists(direction)){
                    System.out.println("This direction does not exist!");
                    return;
                }
                Room nextRoom = currentRoom.getConnectedRoom(direction);
                currentRoom = nextRoom;
                currentRoom.describe();
            }
            case "look" -> {
                currentRoom.describe();
            }
            case "search" -> {
                currentRoom.search();
            }
            case "take" -> {
                ArrayList<Item> items = currentRoom.getItems();
                Item found = items.stream()
                .filter(item -> item.getName().equalsIgnoreCase(part[1]))
                .findFirst()
                .orElse(null);
                if(found == null){
                    System.out.println("This item is nowhere to be seen!");
                    return;
                }
                player.pickupItem(found);
                currentRoom.removeItem(found);
                System.out.println("You have picked up: " + found.describe());
            }
            case "drop" -> {
                Item toDrop = player.getItemByName(part[1]);

                if(toDrop == null){
                    System.out.println("This item is not in your inventory.");
                    return;
                }

                player.dropItem(toDrop);
                currentRoom.addItem(toDrop);

                System.out.println("You have dropped " + toDrop.getName());
            }
            case "equip" -> {
                Item toEquip = player.getItemByName(part[1]);

                if(toEquip == null){
                    System.out.println("This item is not in your inventory!");
                    return;
                }

                switch(toEquip.getItemType()){
                    case WEAPON -> {
                        player.equipWeapon(toEquip);
                    }
                    case ARMOUR -> {
                        player.equipArmour(toEquip);
                    }
                    case POTION -> {
                        System.out.println("You cannot equip a potion");
                        return;
                    }
                }

                System.out.println("You have equipped " + toEquip.getName());

            }
            case "use" -> {
                Item toUse = player.getItemByName(part[1]);

                if(toUse == null){
                    System.out.println("You do not have this item in your inventory!");
                    return;
                }

                if(toUse.getItemType() != ItemTypes.POTION){
                    System.out.println("You can only use a potion!");
                    return;
                }
                player.usePotion(toUse);
            }
            case "stats" -> {
                player.printStats();
            }
            case "inv" -> {
                player.printInventory();
            }
            case "i" -> {
                player.printInventory();
            }
            case "save" -> {
                //delegate to save/load manager
            }
            case "load" -> {
                //delegate to save/load manager
            }
            case "help" -> {
                System.out.println("""
                        ==== HELP MENU ====
                        commands:
                            go [room] - go to a room
                            look - look around
                            search - look for items
                            take [item] - take an item from the room
                            drop [item] - drop an item from your inventory onto the floor
                            equip [item] - equip an item instead of your already equipped item
                            use [potion] - use a potion
                            stats - Show your stats
                            inv/i - show your inventory
                            save - save(WIP)
                            load - load your save (WIP)
                            quit - exit the game
                            help - print this message
                        ===================
                        """);
            }
            case "exit" -> {
                running = false;
                System.out.println("Quitting...");
            }
            case null, default -> {
                System.out.println("""
                        ==== HELP MENU ====
                        commands:
                            go [room] - go to a room
                            look - look around
                            search - look for items
                            take [item] - take an item from the room
                            drop [item] - drop an item from your inventory onto the floor
                            equip [item] - equip an item instead of your already equipped item
                            use [potion] - use a potion
                            stats - Show your stats
                            inv/i - show your inventory
                            save - save(WIP)
                            load - load your save (WIP)
                            quit - exit the game
                            help - print this message
                        ===================
                        """);
            }

        }
    }

}