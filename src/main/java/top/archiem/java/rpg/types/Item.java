package top.archiem.java.rpg.types;

public class Item{
    private ItemTypes type;

    //The value to increase based on the type of item
    private int value;
    private String name;

    public Item(ItemTypes type, int value, String name){
        this.type = type;
        this.value = value;
        this.name = name;
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
}