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
                System.out.println("Failed all attempts to load save file");
            }
        }
    }

    public void save(HashMap<String, Room> rooms, Player player, Room currentRoom, ArrayList<ShopItem> shopItems){
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
            System.out.println(AnsiColors.green("Saved successfully to " + saveFile.getCanonicalPath()));
        } catch (Exception e){

        }
        
    }


}