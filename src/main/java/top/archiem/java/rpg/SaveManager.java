package top.archiem.java.rpg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;
import top.archiem.java.rpg.types.Save;
import top.archiem.java.rpg.types.SaveTypes;
import top.archiem.java.rpg.types.ShopItem;

public class SaveManager{

    File saveFile;

    public void initSaves(){
        try{
            saveFile = new File("save.txt");
            saveFile.createNewFile();
        } catch (Exception e){
            try {
                FileWriter tempWriter = new FileWriter("save.txt");
                saveFile = new File("save.txt");
            } catch (IOException ex){
                System.out.println(AnsiColors.red("Failed all attempts to load save file."));
            }
        }
    }

    public void save(HashMap<String, Room> rooms, Player player, Room currentRoom, ArrayList<ShopItem> shopItems){
        Item equippedArmour = player.getEquippedArmour();
        Item equippedWeapon = player.getEquippedWeapon();
        if(saveFile == null){
            System.out.println(AnsiColors.red("Save system not initialised"));
            return;
        }
        System.out.println(AnsiColors.red("Saving... Do not close the terminal!"));
        try(PrintWriter writer = new PrintWriter(new FileWriter(saveFile), true)){
            writer.println(SaveTypes.NAME.toString() + ":" + player.getName());
            writer.println(SaveTypes.XP.toString() + ":" + player.getXp());
            writer.println(SaveTypes.LEVEL.toString() + ":" + player.getLevel());
            writer.println(SaveTypes.HP.toString() + ":" + player.getHp() + ":" + player.getMaxHp());
            writer.println(SaveTypes.ATTACK.toString() + ":" + player.getAttack());
            writer.println(SaveTypes.DEFENSE.toString() + ":" + player.getDefence());
            writer.println(SaveTypes.GOLD + ":" + player.getGold());
            writer.println(SaveTypes.WEAPON + ":" + equippedWeapon.getName() + ":" + equippedWeapon.getItemType().toString() + ":" + equippedWeapon.getValue() + ":" + equippedWeapon.getGoldValue());
            writer.println(SaveTypes.ARMOUR + ":" + equippedArmour.getName() + ":" + equippedArmour.getItemType().toString() + ":" + equippedArmour.getValue() + ":" + equippedArmour.getGoldValue());
            for(Item i : player.getInventory()){
                writer.println(SaveTypes.ITEM.toString() + ":" + i.getName() + ":" + i.getItemType().toString() + ":" + i.getValue() + ":" + i.getGoldValue());
            }
            for(Room r : rooms.values()){
                if(!r.hasAliveEnemies()){
                    writer.println(SaveTypes.CLEARED.toString() + ":" + r.getName());
                }
                if(r.getItems().size() > 0){
                    for(Item i : r.getItems()){
                        writer.println(SaveTypes.PICKUP.toString() + ":" + r.getName() + ":" + i.getName() + ":" + i.getItemType().toString() + ":" + i.getValue() + ":" + i.getGoldValue());
                    }
                }
            }
            for(ShopItem i : shopItems){
                writer.println(SaveTypes.SHOPITEM.toString() + ":" + i.getName() + ":" + i.getItemType().toString() + ":" + i.getValue() + ":" + i.getGoldValue() + ":" + i.getCost());
            }
            System.out.println(AnsiColors.green("Saved successfully to " + saveFile.getCanonicalPath()));
        } catch (Exception e){

        } 
    }

    public Save load(){
        if(saveFile == null){
            System.out.println("Save system not initialised");
            return null;
        }

        return null;
        
    }


}