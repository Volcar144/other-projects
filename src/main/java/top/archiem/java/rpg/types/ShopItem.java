package top.archiem.java.rpg.types;

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
        return "[" + getItemType().toString() + "] " + getName() + " - +" + String.valueOf(getValue()) + " " + getStat() + " cost: " + cost + " gold";
    }

    public int getCost(){
        return cost;
    }

}