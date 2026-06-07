package top.archiem.java.rpg.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class Enemy extends Entity{

    String name;

    private int goldReward;
    private int xpReward;
    private double lootChance;
    private ArrayList<Item> lootpool = new ArrayList<>();
    

    public Enemy(String name, int hp, int maxHp, int defense, int attack, int goldReward, int xpReward, double lootChance){
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
        this.defense = defense;
        this.attack = attack;
        this.goldReward = goldReward;
        this.xpReward = xpReward;
        this.lootChance = lootChance;
        lootpool.add(new Item(ItemTypes.ARMOUR, 3, "Rusty Plate", 4));
        lootpool.add(new Item(ItemTypes.WEAPON, 7, "Copper Sword", 15));
        lootpool.add(new Item(ItemTypes.WEAPON, 7, "Iron Sword", 15));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 50, "Large Potion", 30));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 25, "Small Potion", 20));
        lootpool.add(new Item(ItemTypes.POTION, 50, "Large Potion", 30));
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void addLoot(Item item){
        lootpool.add(item);
    }

    public Item rollLoot(){
        Random r = new Random();
        double out = r.nextDouble(0.0, 1.0);
        if(lootChance > round(out, 2)){
            int itemNum = r.nextInt(0, lootpool.size());
            return lootpool.get(itemNum);
        } else {
            return null;
        }
    }

    public static Enemy zombie(){
        return new Enemy("zombie", 25, 25, 1, 6, 10, 30, 0.16);
    }

    public static Enemy ghost(){
        return new Enemy("ghost",35, 35, 3, 10, 20, 45, 0.31);
    }

    public static Enemy skeleton(){
        return new Enemy("skeleton",40, 40, 2, 14, 30, 60, 0.42);
    }

    public static Enemy dragon(){
        return new Enemy("dragon",90, 90, 6, 19, 45, 120, 1.0);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
