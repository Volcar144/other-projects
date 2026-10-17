package top.archiem.java.rpg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import top.archiem.java.rpg.errors.SaveLoadException;
import top.archiem.java.rpg.types.*;
import top.archiem.java.rpg.helpers.Animations;
import top.archiem.java.rpg.helpers.ConsoleTyper;

public class GameEngine {
    Player player;
    boolean running = false;
    HashMap<String, Room> rooms;
    Room currentRoom;
    String command;
    SaveManager saveManager;
    ArrayList<ShopItem> shopItems;
    ConsoleTyper tp;

    public void startGameLoop() {
        tp = new ConsoleTyper(System.out, System.in);
        tp.setWrapWidth(80);

        shopItems = new ArrayList<>();
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 02, "Broken Sword", 1, 5));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 05, "Rusty Sword", 6, 9));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 07, "Steel Sword", 13, 19));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 10, "Broadsword", 20, 30));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 15, "Enchanted Sword", 27, 40));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 04, "Leather Armour", 7, 10));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 07, "Chainmail Armour", 10, 20));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 10, "Plate Armour", 20, 30));
        shopItems.add(new ShopItem(ItemTypes.POTION, 15, "Small Potion", 10, 20));
        shopItems.add(new ShopItem(ItemTypes.POTION, 25, "Medium Potion", 15, 25));
        shopItems.add(new ShopItem(ItemTypes.POTION, 50, "Large Potion", 20, 30));
        shopItems.add(new ShopItem(ItemTypes.POTION, 100, "Huge Potion", 30, 40));
        running = true;
        saveManager = new SaveManager();
        saveManager.initSaves();

        Scanner keyboard = new Scanner(System.in);

        Animations.clearScreen();
        msg(AnsiColors.cyan("Loading map..."));
        rooms = WorldLoader.loadWorld("world.txt");
        currentRoom = rooms.get(WorldLoader.getStartRoom());

        msg(AnsiColors.bold(AnsiColors.blue(WorldLoader.getStartRoom().toString())));
        Animations.titleScreen(tp);
        narrate(AnsiColors.green("Welcome to " + AnsiColors.bold("jaRpg") + ", an interactive dungeon crawler game"));
        msg(AnsiColors.yellow("To start, create a name for your player: "));
        String name = keyboard.nextLine().trim();
        player = new Player(name);

        narrate(AnsiColors.green("Nice to meet you " + AnsiColors.bold(name) + ". Let's start your adventure"));
        narrate(AnsiColors.magenta("You are just entered a dungeon "));
        currentRoom.describe();
        msg(AnsiColors.blue("What do you want to do (type help if you need it)"));
        String command = keyboard.nextLine().trim();
        parseCommand(command, keyboard);

        while (running) {
            msg(AnsiColors.blue("Take an action: "));
            command = keyboard.nextLine().trim();
            parseCommand(command, keyboard);
        }
        keyboard.close();
    }

    private void narrate(String text) {
        tp.setSpeed(ConsoleTyper.Speed.NORMAL);
        tp.println(text);
    }

    private void msg(String text) {
        tp.setSpeed(ConsoleTyper.Speed.INSTANT);
        tp.println(text);
    }

    private void parseCommand(String input, Scanner keyboard) {
        String help = "help";
        String[] part = {help};
        try {
            part = input.split(" ", 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            printHelp();
        }

        switch (part[0].toLowerCase()) {
            case "north" -> {
                String direction = "north";
                handleRoom(keyboard, direction);
            }
            case "south" -> {
                String direction = "south";
                handleRoom(keyboard, direction);
            }
            case "east" -> {
                String direction = "east";
                handleRoom(keyboard, direction);
            }
            case "west" -> {
                String direction = "west";
                handleRoom(keyboard, direction);
            }
            case "look" -> {
                currentRoom.describe();
            }
            case "search" -> {
                List<Item> toRemove = new ArrayList<>();
                currentRoom.search();
                for (Item i : currentRoom.getItems()) {
                    player.pickupItem(i);
                    narrate(AnsiColors.green("You have picked up: " + i.describe()));
                    toRemove.add(i);
                }
                currentRoom.getItems().removeAll(toRemove);
            }
            case "drop" -> {
                try {
                    Item toDrop = player.getItemByName(part[1]);

                    if (toDrop == null) {
                        msg(AnsiColors.red("This item is not in your inventory."));
                        return;
                    }

                    player.dropItem(toDrop);
                    currentRoom.addItem(toDrop);

                    msg(AnsiColors.yellow("You have dropped " + toDrop.getName()));
                } catch (ArrayIndexOutOfBoundsException e) {
                    printHelp();
                }
            }
            case "equip" -> {
                try {
                    Item toEquip = player.getItemByName(part[1]);

                    if (toEquip == null) {
                        msg(AnsiColors.red("This item is not in your inventory!"));
                        return;
                    }

                    switch (toEquip.getItemType()) {
                        case WEAPON -> {
                            player.equipWeapon(toEquip);
                        }
                        case ARMOUR -> {
                            player.equipArmour(toEquip);
                        }
                        case POTION -> {
                            msg(AnsiColors.red("You cannot equip a potion"));
                            return;
                        }
                    }

                    msg(AnsiColors.green("You have equipped " + AnsiColors.bold(toEquip.getName())));
                } catch (ArrayIndexOutOfBoundsException e) {
                    printHelp();
                }
            }
            case "use" -> {
                try {
                    Item toUse = player.getItemByName(part[1]);

                    if (toUse == null) {
                        msg(AnsiColors.red("You do not have this item in your inventory!"));
                        return;
                    }

                    if (toUse.getItemType() != ItemTypes.POTION) {
                        msg(AnsiColors.red("You can only use a potion!"));
                        return;
                    }
                    player.usePotion(toUse);
                    Integer value = toUse.getValue();
                    narrate(AnsiColors.green("You have used the " + AnsiColors.bold(toUse.getName())
                            + " and have healed " + AnsiColors.bold(value.toString() + " health")));
                } catch (ArrayIndexOutOfBoundsException e) {
                    printHelp();
                }
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
                saveManager.save(rooms, player, currentRoom, shopItems);
            }
            case "load" -> {
                try {
                    Save save = saveManager.load(rooms);
                    shopItems = save.getShopItems();
                    currentRoom = save.getCurrentRoom();
                    player = save.getPlayer();
                    rooms = save.getUpdatedRooms();
                    narrate(save.toString());
                } catch (SaveLoadException se) {
                    msg(AnsiColors.red("Load failed! " + se.getMessage()));
                } catch (Exception e) {
                    msg(AnsiColors.red("Load failed! " + e.getMessage()));
                }
            }
            case "shop" -> {
                if (currentRoom.isShop()) {
                    handleShop(currentRoom, keyboard);
                } else {
                    msg(AnsiColors.red("Current room is not a shop!"));
                }
            }
            case "quit" -> {
                running = false;
                msg(AnsiColors.red("Quitting..."));
            }
            case null, default -> {
                printHelp();
            }
        }
    }

    private void printHelp() {
        msg(AnsiColors.cyan("==== HELP MENU ===="));
        msg("commands:");
        msg("    " + AnsiColors.yellow("north/south/east/west") + " - go to a room");
        msg("    " + AnsiColors.yellow("look") + " - look around");
        msg("    " + AnsiColors.yellow("search") + " - look for items");
        msg("    " + AnsiColors.yellow("drop [item]") + " - drop an item from your inventory onto the floor");
        msg("    " + AnsiColors.yellow("equip [item]") + " - equip an item instead of your already equipped item");
        msg("    " + AnsiColors.yellow("use [potion]") + " - use a potion");
        msg("    " + AnsiColors.yellow("stats") + " - Show your stats");
        msg("    " + AnsiColors.yellow("inv/i") + " - show your inventory");
        msg("    " + AnsiColors.yellow("save") + " - save(WIP)");
        msg("    " + AnsiColors.yellow("load") + " - load your save (WIP)");
        msg("    " + AnsiColors.yellow("quit") + " - exit the game");
        msg("    " + AnsiColors.yellow("shop") + " - open the shop if in a shop room.");
        msg("    " + AnsiColors.yellow("help") + " - print this message");
        msg(AnsiColors.cyan("==================="));
    }

    private void handleRoom(Scanner keyboard, String direction) {
        if (!currentRoom.connectionExists(direction)) {
            msg(AnsiColors.red("This direction does not exist!"));
            return;
        }
        Room nextRoom = currentRoom.getConnectedRoom(direction);
        boolean move = false;
        if (nextRoom.hasEnemies() && nextRoom.isBossRoom()) {
            BattleSystem.runBattle(nextRoom, keyboard, player, tp);
            if (!player.isAlive()) {
                narrate(AnsiColors.red("You died..."));
                running = false;
            } else {
                narrate(AnsiColors.green("You beat the boss, you win!"));
                running = false;
            }
        }
        if (nextRoom.hasEnemies()) {
            move = BattleSystem.runBattle(nextRoom, keyboard, player, tp);
        }

        if (!move) {
            if (!player.isAlive()) {
                narrate(AnsiColors.red("You died..."));
                running = false;
            }
            currentRoom = nextRoom;
            currentRoom.describe();
        }
        if (currentRoom.isShop()) {
            handleShop(currentRoom, keyboard);
        }
    }

    private void handleShop(Room room, Scanner keyboard) {

        narrate(AnsiColors.green("Welcome to the " + AnsiColors.bold(room.getName()) + ", a shop"));
        boolean inShop = true;
        while (inShop) {
            int choice = 0;
            do {
                msg(AnsiColors.yellow("You have " + player.getGold() + " gold."));
                msg(AnsiColors.blue("What would you like to do: "));
                msg(AnsiColors.yellow("[1] Buy | [2] Sell | [3] exit"));
                try {
                    choice = Integer.parseInt(keyboard.next());
                } catch (Exception e) {
                    // Ignore
                }
            } while (choice != 1 && choice != 2 && choice != 3);

            switch (choice) {
                case 1 -> {
                    msg(AnsiColors.cyan("Items in stock: "));
                    for (ShopItem i : shopItems) {
                        int location = shopItems.indexOf(i) + 1;
                        msg(AnsiColors.yellow("[" + location + "] ") + i.describe());
                    }
                    int num = 900;
                    do {
                        msg(AnsiColors.blue("What item to buy (0 to exit)"));
                        try {
                            num = Integer.parseInt(keyboard.next());
                        } catch (Exception e) {
                            // Ignore
                        }
                    } while (num == 900 || num < 0 || num > shopItems.size());
                    if (num == 0) {
                        break;
                    }
                    int index = num - 1;
                    ShopItem toBuy = shopItems.get(index);
                    if (!(player.getGold() >= toBuy.getCost())) {
                        int shortage = toBuy.getCost() - player.getGold();
                        msg(AnsiColors.red("You are " + shortage + " gold short of this!"));
                        break;
                    }
                    Item toGive = toBuy.toItem();
                    player.pickupItem(toGive);
                    narrate(AnsiColors.green("You received " + AnsiColors.bold(toGive.getName())));
                }
                case 2 -> {
                    if (player.getInventory().isEmpty()) {
                        msg(AnsiColors.yellow("You have no items to sell."));
                        break;
                    }
                    msg(AnsiColors.cyan("Your inventory: "));
                    for (Item i : player.getInventory()) {
                        int location = player.getInventory().indexOf(i) + 1;
                        msg(AnsiColors.yellow("[" + location + "] ") + i.describe());
                    }
                    int num = 900;
                    do {
                        msg(AnsiColors.blue("What item to sell (0 to exit)"));
                        try {
                            num = Integer.parseInt(keyboard.next());
                        } catch (Exception e) {
                            // Ignore
                        }
                    } while (num == 900 || num < 0 || num > player.getInventory().size());
                    if (num == 0) {
                        break;
                    }
                    int index = num - 1;
                    Item toSell = player.getInventory().get(index);
                    int value = toSell.getGoldValue();
                    player.getInventory().remove(toSell);
                    player.addGold(value);

                    ShopItem asShopItem = new ShopItem(toSell.getItemType(), toSell.getValue(), toSell.getName(),
                            toSell.getGoldValue(), toSell.getGoldValue() + 10);
                    shopItems.add(asShopItem);
                    narrate(AnsiColors.green("Sold " + AnsiColors.bold(toSell.getName()) + " for " + value + " gold."));
                }
                case 3 -> {
                    inShop = false;
                    narrate(AnsiColors.magenta("Left the shop..."));
                }
            }
        }
    }
}