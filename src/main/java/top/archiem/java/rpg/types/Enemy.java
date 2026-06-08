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

    public static Enemy forestSpider(){
        return new Enemy("forest spider",18, 18, 0, 5, 8, 18, 0.18);
    }

    public static Enemy skeletonWarrior(){
        return new Enemy("skeleton warrior",48, 48, 3, 16, 35, 70, 0.45);
    }

    public static Enemy ghoul(){
        return new Enemy("ghoul",44, 44, 4, 12, 25, 50, 0.35);
    }

    public static Enemy spectralLibrarian(){
        return new Enemy("spectral librarian",36, 36, 5, 14, 40, 60, 0.40);
    }

    public static Enemy shadowKnight(){
        return new Enemy("shadow knight",52, 52, 6, 17, 50, 75, 0.45);
    }

    public static Enemy darkMage(){
        return new Enemy("dark mage",30, 30, 2, 18, 30, 65, 0.50);
    }

    public static Enemy slime(){
        return new Enemy("slime",20, 20, 1, 3, 5, 10, 0.20);
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
