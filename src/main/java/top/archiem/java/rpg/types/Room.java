package top.archiem.java.rpg.types;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {

    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    HashMap<String, Room> connections = new HashMap<>();

    public Room(ArrayList<Enemy> enemies, ArrayList<Item> items, HashMap<String,Room> connections){
        this.enemies = enemies;
        this.items = items;
        this.connections = connections;
    }

    public void describe(){
        System.out.println("In this room you can goin one of the following directions: ");
        for(String i: connections.keySet()){
            System.out.println(i + ",");
        }
        System.out.println("\nIn this room you can see the following items: ");
        for(Item i: items){
            System.out.println(i.getName() + ",");
        }
    }

}