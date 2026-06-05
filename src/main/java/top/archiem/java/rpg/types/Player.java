package top.archiem.java.rpg.types;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private int hp = 100;
    private int maxHp = 100;
    private int defense = 0;
    private int attack = 05;
    private int xp = 0;
    private int gold = 0;
    private int level = 0;

    private ArrayList<Item> inventory = new ArrayList<>();
    private Item equippedWeapon;
    private Item equippedArmour;

    String name = "Player";

    public Player(String name){
        this.name = name;
        
    }

    public int getHp(){
        return hp;
    }

    public List getInventory(){
        return inventory;
    }

    public void pickupItem(Item item){
        inventory.add(item);
    }

    public void dropItem(Item item){
        if(!inventory.contains(item)){
            return;
        }
        inventory.remove(item);
    }

    public void equipWeapon(Item item){
        if(item.getItemType() != ItemTypes.WEAPON){
            return;
        }
        if(!equippedWeapon.equals(null)){
            inventory.add(equippedWeapon);
            attack -= equippedWeapon.getValue();
        }
        if(inventory.contains(item)){
            inventory.remove(item);
        }
        equippedWeapon = item;
        attack += equippedWeapon.getValue();
    }

    public void equipArmour(Item item){
        if(item.getItemType() != ItemTypes.ARMOUR){
            return;
        }
        if(!equippedArmour.equals(null)){
            inventory.add(equippedArmour);
            defense -= equippedArmour.getValue();
        }
        if(inventory.contains(item)){
            inventory.remove(item);
        }
        equippedArmour = item;
        defense += equippedArmour.getValue();
    }

    public void usePotion(Item item){
        if(!inventory.contains(item)){
            return;
        } else if(item.getItemType() != ItemTypes.POTION){
            return;
        }
        hp += item.getValue();
        inventory.remove(item);
    }

    public void addXp(int toAdd){
        int tempTotal = xp += toAdd;
        if(tempTotal >= 500){
            levelUp();
            tempTotal -= 500;
            xp = tempTotal;
        } else {
            xp = tempTotal;
        }
    }

    public void levelUp(){
        level ++;

        maxHp += 10;
        hp = maxHp;
        
        attack += 5;
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

    public void printInventory(){
        System.out.println("==== INVENTORY ====");
        for(Item item : inventory){
            System.out.println(item.getName() + ",");
        }
        System.out.println("===================");
    }

    public void printStats(){
        System.out.printf("""
                ==== STATS ====
                Hp: %i
                Max Hp: %i
                Defense: %i
                Attack: %i
                ===============
                """, hp,maxHp, defense, attack);
    }

}