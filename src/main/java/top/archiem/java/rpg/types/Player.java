package top.archiem.java.rpg.types;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private int hp = 100;
    private int maxHp = 100;
    private int defense = 0;
    private int attack = 20;
    private int xp = 0;
    private int gold = 0;
    private int level = 0;

    private ArrayList<Item> inventory = new ArrayList<>();

    String name = "Player";

    public void heal(int amount){
        int tempHealth = hp + amount;
        if(tempHealth > maxHp){
            hp = maxHp;
            return;
        }
        hp = tempHealth;
    }

    public int getHp(){
        return hp;
    }

    public List getInventory(){
        return inventory;
    }

    public void equip(Item item){
        switch (item.getItemType()) {
            case WEAPON -> {
                attack += item.getValue();
                inventory.add(item);
            }
            case ARMOUR -> {
                defense += item.getValue();
                inventory.add(item);
            }
            case POTION -> {
                heal(item.getValue());
            }
        }
    }

    public void unEquip(Item item){
        if(!inventory.contains(item)){
            return;
        }
        switch(item.getItemType()){
            case WEAPON -> {
                attack -= item.getValue();
                inventory.remove(item);
            }
            case ARMOUR -> {
                defense -= item.getValue();
                inventory.remove(item);
            }
        }
    }

    public void addXp(int toAdd){
        int tempTotal = xp += toAdd;
        if(tempTotal >= 500){
            level ++;
            tempTotal -= 500;
            xp = tempTotal;
        } else {
            xp = tempTotal;
        }
    }

    public int getLevel(){
        return level;
    }

    public void addGold(int toAdd){
        gold += toAdd;
    }

    public int getGold(){
        return gold;
    }
    public void removeGold(int toRemove){
        gold -= toRemove;
    }

}