package top.archiem.java.rpg.types;

public class Item{
    private ItemTypes type;

    //The value to increase based on the type of item
    private int value;
    private String name;
    private int goldValue;

    public Item(ItemTypes type, int value, String name, int goldValue){
        this.type = type;
        this.value = value;
        this.name = name;
        this.goldValue = goldValue;
    }

    public ItemTypes getItemType(){
        return type;
    }

    public int getValue(){
        return value;
    }

    public String getName(){
        return name;
    }

    public int getGoldValue(){
        return goldValue;
    }

    public String getStat(){
        switch(type){
            case POTION -> {
                return "health";
            }
            case ARMOUR -> {
                return "defense";
            }
            case WEAPON -> {
                return "attack";
            }

        }
        return "unknown";
    }

    public String describe(){
        return "[" + type.toString() + "] " + name + " - +" + String.valueOf(value) + " " + getStat() + " (" + String.valueOf(goldValue) + " gold)";
    }
}