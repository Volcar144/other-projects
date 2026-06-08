package top.archiem.java.rpg.types;

import top.archiem.java.rpg.AnsiColors;

public class ShopItem extends Item{
    int cost;

    public ShopItem(ItemTypes type, int value, String name, int goldValue, int cost){
        super(type, value, name, goldValue);
        this.cost = cost;
    }

    public Item toItem(){
        return new Item(getItemType(), getValue(), getName(), getGoldValue());
    }

    @Override
    public String describe() {
        return super.describe() + " " + AnsiColors.yellow("cost: " + cost + " gold");
    }

    public int getCost(){
        return cost;
    }

}