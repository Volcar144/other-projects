package top.archiem.java.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;
import top.archiem.java.rpg.types.ShopItem;

public class GameEngine {
    Player player;
    boolean running = false;
    HashMap<String, Room> rooms;
    Room currentRoom;
    String command;


    public void startGameLoop() {
        running = true;

        Scanner keyboard = new Scanner(System.in);
        System.out.println(AnsiColors.cyan("Loading map..."));
        rooms = WorldLoader.loadWorld("world.txt");
        currentRoom = rooms.get(WorldLoader.getStartRoom());
        System.out.println(AnsiColors.bold(AnsiColors.blue(WorldLoader.getStartRoom().toString())));
        System.out.println(AnsiColors.green("Welcome to " + AnsiColors.bold("jaRpg") + ", an interactive dungeon crawler game"));
        System.out.println(AnsiColors.yellow("To start, create a name for your player: "));
        String name = keyboard.nextLine().trim();
        player = new Player(name);
        System.out.println(AnsiColors.green("Nice to meet you " + AnsiColors.bold(name) + ". Let's start your adventure"));
        System.out.println(AnsiColors.magenta("You are just entered a dungeon "));
        currentRoom.describe();
        System.out.println(AnsiColors.blue("What do you want to do (type help if you need it)"));
        String command = keyboard.nextLine().trim();
        parseCommand(command, keyboard);
        while(running){
            System.out.println(AnsiColors.blue("Take an action: "));
            command = keyboard.nextLine().trim();
            parseCommand(command, keyboard);
        }
        keyboard.close();


    }

    private void parseCommand(String input, Scanner keyboard){
        String[] part = input.split(" ",2);

        switch(part[0].toLowerCase()){
            case "go" -> {
                String direction = part[1];
                if(!currentRoom.connectionExists(direction)){
                    System.out.println(AnsiColors.red("This direction does not exist!"));
                    return;
                }
                Room nextRoom = currentRoom.getConnectedRoom(direction);
                boolean move = false;
                if(nextRoom.hasEnemies() && nextRoom.isBossRoom()){
                    BattleSystem.runBattle(nextRoom, keyboard, player);
                    if(!player.isAlive()){
                        System.out.println(AnsiColors.red("You died..."));
                        running = false;
                    } else {
                        System.out.println(AnsiColors.green("You beat the boss, you win!"));
                        running = false;
                    }
                }
                if(nextRoom.hasEnemies()){
                    move = BattleSystem.runBattle(nextRoom, keyboard, player);
                }
                
                if(!move){
                    if(!player.isAlive()){
                        System.out.println(AnsiColors.red("You died..."));
                        running = false;
                    }
                    currentRoom = nextRoom;
                    currentRoom.describe();
                }
                if(currentRoom.isShop()){
                    handleShop(currentRoom, keyboard);
                }
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
                    System.out.println(AnsiColors.red("This item is nowhere to be seen!"));
                    return;
                }
                player.pickupItem(found);
                currentRoom.removeItem(found);
                System.out.println(AnsiColors.green("You have picked up: " + found.describe()));
            }
            case "drop" -> {
                Item toDrop = player.getItemByName(part[1]);

                if(toDrop == null){
                    System.out.println(AnsiColors.red("This item is not in your inventory."));
                    return;
                }

                player.dropItem(toDrop);
                currentRoom.addItem(toDrop);

                System.out.println(AnsiColors.yellow("You have dropped " + toDrop.getName()));
            }
            case "equip" -> {
                Item toEquip = player.getItemByName(part[1]);

                if(toEquip == null){
                    System.out.println(AnsiColors.red("This item is not in your inventory!"));
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
                        System.out.println(AnsiColors.red("You cannot equip a potion"));
                        return;
                    }
                }

                System.out.println(AnsiColors.green("You have equipped " + AnsiColors.bold(toEquip.getName())));

            }
            case "use" -> {
                Item toUse = player.getItemByName(part[1]);

                if(toUse == null){
                    System.out.println(AnsiColors.red("You do not have this item in your inventory!"));
                    return;
                }

                if(toUse.getItemType() != ItemTypes.POTION){
                    System.out.println(AnsiColors.red("You can only use a potion!"));
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
            case "shop" -> {
                if(currentRoom.isShop()){
                    handleShop(currentRoom, keyboard);
                } else {
                    System.out.println(AnsiColors.red("Current room is not a shop!"));
                }
            }
            case "help" -> {
                System.out.println(AnsiColors.cyan("==== HELP MENU ===="));
                System.out.println("commands:");
                System.out.println("    " + AnsiColors.yellow("go [room]") + " - go to a room");
                System.out.println("    " + AnsiColors.yellow("look") + " - look around");
                System.out.println("    " + AnsiColors.yellow("search") + " - look for items");
                System.out.println("    " + AnsiColors.yellow("take [item]") + " - take an item from the room");
                System.out.println("    " + AnsiColors.yellow("drop [item]") + " - drop an item from your inventory onto the floor");
                System.out.println("    " + AnsiColors.yellow("equip [item]") + " - equip an item instead of your already equipped item");
                System.out.println("    " + AnsiColors.yellow("use [potion]") + " - use a potion");
                System.out.println("    " + AnsiColors.yellow("stats") + " - Show your stats");
                System.out.println("    " + AnsiColors.yellow("inv/i") + " - show your inventory");
                System.out.println("    " + AnsiColors.yellow("save") + " - save(WIP)");
                System.out.println("    " + AnsiColors.yellow("load") + " - load your save (WIP)");
                System.out.println("    " + AnsiColors.yellow("quit") + " - exit the game");
                System.out.println("    " + AnsiColors.yellow("help") + " - print this message");
                System.out.println(AnsiColors.cyan("==================="));
            }
            case "quit" -> {
                running = false;
                System.out.println(AnsiColors.red("Quitting..."));
            }
            case null, default -> {
                System.out.println(AnsiColors.cyan("==== HELP MENU ===="));
                System.out.println("commands:");
                System.out.println("    " + AnsiColors.yellow("go [room]") + " - go to a room");
                System.out.println("    " + AnsiColors.yellow("look") + " - look around");
                System.out.println("    " + AnsiColors.yellow("search") + " - look for items");
                System.out.println("    " + AnsiColors.yellow("take [item]") + " - take an item from the room");
                System.out.println("    " + AnsiColors.yellow("drop [item]") + " - drop an item from your inventory onto the floor");
                System.out.println("    " + AnsiColors.yellow("equip [item]") + " - equip an item instead of your already equipped item");
                System.out.println("    " + AnsiColors.yellow("use [potion]") + " - use a potion");
                System.out.println("    " + AnsiColors.yellow("stats") + " - Show your stats");
                System.out.println("    " + AnsiColors.yellow("inv/i") + " - show your inventory");
                System.out.println("    " + AnsiColors.yellow("save") + " - save(WIP)");
                System.out.println("    " + AnsiColors.yellow("load") + " - load your save (WIP)");
                System.out.println("    " + AnsiColors.yellow("quit") + " - exit the game");
                System.out.println("    " + AnsiColors.yellow("shop") + " - open the shop if in a shop room.");
                System.out.println("    " + AnsiColors.yellow("help") + " - print this message");
                System.out.println(AnsiColors.cyan("==================="));
            }

        }
    }

    private void handleShop(Room room, Scanner keyboard){
        ArrayList<ShopItem> shopItems = new ArrayList<>();
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 02,"Broken Sword", 1,5));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 05,"Rusty Sword", 6,9));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 07,"Steel Sword", 13,19));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 10,"Broadsword", 20,30));
        shopItems.add(new ShopItem(ItemTypes.WEAPON, 15,"Enchanted Sword", 27,40));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 04,"Leather Armour", 7,10));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 07,"Chainmail Armour", 10,20));
        shopItems.add(new ShopItem(ItemTypes.ARMOUR, 10,"Plate Armour", 20,30));
        shopItems.add(new ShopItem(ItemTypes.POTION, 15,"Small Potion", 10,20));
        shopItems.add(new ShopItem(ItemTypes.POTION, 25,"Medium Potion", 15,25));
        shopItems.add(new ShopItem(ItemTypes.POTION, 50,"Large Potion", 20,30));
        shopItems.add(new ShopItem(ItemTypes.POTION, 100,"Huge Potion", 30,40));

        System.out.println(AnsiColors.green("Welcome to the " + AnsiColors.bold(room.getName()) + ", a shop"));
        boolean inShop = true;
        while(inShop){
        int choice = 0;
        do{
            System.out.println(AnsiColors.blue("What would you like to do: "));
            System.out.println(AnsiColors.yellow("[1] Buy | [2] Sell | [3] exit"));
            try {
                choice = Integer.parseInt(keyboard.next());
            } catch (Exception e) {
                //  Ignore
            }
        } while(choice != 1 && choice != 2 && choice != 3);

        switch(choice){
            case 1 -> {
                System.out.println(AnsiColors.cyan("Items in stock: "));
                for(ShopItem i : shopItems){
                    int location = shopItems.indexOf(i) + 1;
                    System.out.println(AnsiColors.yellow("["+ location + "] ") + i.describe());
                }
                int num = 900;
                do{
                    System.out.println(AnsiColors.blue("What item to buy (0 to exit)"));
                    try {
                        num = Integer.parseInt(keyboard.next());
                    } catch (Exception e) {
                        //  Ignore
                    }
                } while (num == 900 || num < 0 || num > shopItems.size());
                if(num == 0){
                    break;
                }
                int index = num - 1;
                ShopItem toBuy = shopItems.get(index);
                if(!(player.getGold() >= toBuy.getCost())){
                    int shortage = toBuy.getCost() - player.getGold();
                    System.out.println(AnsiColors.red("You are " + shortage + " gold short of this!"));
                    break;
                }
                Item toGive = toBuy.toItem();
                player.pickupItem(toGive);
                System.out.println(AnsiColors.green("You received " + AnsiColors.bold(toGive.getName())));
            }
            case 2 -> {
                if(player.getInventory().isEmpty()){
                    System.out.println(AnsiColors.yellow("You have no items to sell."));
                    break;
                }
                System.out.println(AnsiColors.cyan("Your inventory: "));
                for(Item i : player.getInventory()){
                    int location = player.getInventory().indexOf(i) + 1;
                    System.out.println(AnsiColors.yellow("["+ location + "] ") + i.describe());
                }
                int num = 900;
                do{
                    System.out.println(AnsiColors.blue("What item to sell (0 to exit)"));
                    try {
                        num = Integer.parseInt(keyboard.next());
                    } catch (Exception e) {
                        //  Ignore
                    }
                } while (num == 900 || num < 0 || num > player.getInventory().size());
                if(num == 0){
                    break;
                }
                int index = num - 1;
                Item toSell = player.getInventory().get(index);
                int value = toSell.getGoldValue();
                player.getInventory().remove(toSell);
                player.addGold(value);

                ShopItem asShopItem = new ShopItem(toSell.getItemType(), toSell.getValue(), toSell.getName(), toSell.getGoldValue(),toSell.getGoldValue() + 10 );
                shopItems.add(asShopItem);
                System.out.println(AnsiColors.green("Sold " + AnsiColors.bold(toSell.getName()) + " for " + value + " gold."));

            }
            case 3 -> {
                inShop = false;
                System.out.println(AnsiColors.magenta("Left the shop..."));
            }
        }}

    }

}