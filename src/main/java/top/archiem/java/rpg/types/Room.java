package top.archiem.java.rpg.types;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {

    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    HashMap<String, Room> connections = new HashMap<>();
    String description;
    String name;
    boolean isBossRoom = false;
    boolean isShop = false;
    boolean isStart = false;

    public Room(ArrayList<Enemy> enemies, ArrayList<Item> items, HashMap<String,Room> connections, String description, String name){
        this.enemies = enemies;
        this.items = items;
        this.connections = connections;
        this.description = description;
        this.name = name;
    }

    public Room(ArrayList<Enemy> enemies, ArrayList<Item> items, HashMap<String,Room> connections, String description, String name, boolean isShop){
        this.enemies = enemies;
        this.items = items;
        this.connections = connections;
        this.description = description;
        this.isShop = isShop;
        this.name = name;
    }
    
    public Room(ArrayList<Enemy> enemies, ArrayList<Item> items, HashMap<String,Room> connections, String description, String name, boolean isShop, boolean isBossRoom){
        this.enemies = enemies;
        this.items = items;
        this.connections = connections;
        this.description = description;
        this.name = name;
        this.isShop = isShop;
        this.isBossRoom = isBossRoom;
    }

    public void describe(){
        System.out.println("You walk into " + description);
        System.out.println("In this room you can go in one of the following directions: ");
        for(String i: connections.keySet()){
            System.out.println(i + ",");
        }
    }

    public void search(){
        System.out.print("You search high and low to find");
        if(items.isEmpty()){
            System.out.println(" nothing.");
            return;
        }
        System.out.println(":");

        for(Item i: items){
            System.out.println(i.getName() + ",");
        }

    }

    public String getName(){
        return name;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean hasEnemies(){
        return !enemies.isEmpty();
    }

    public boolean connectionExists(String direction){
        return connections.containsKey(direction);
    }

    public Room getConnectedRoom(String direction){
        return connections.get(direction);
    }

    public void addExit(String direction, Room room){
        connections.put(direction, room);
    }

    public void addEnemy(Enemy enemy){
        enemies.add(enemy);
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void removeItem(Item item){
        if(items.contains(item)){
            items.remove(item);
        }
    }

    public boolean hasAliveEnemies(){
        for(Enemy i : enemies){
            if(i.isAlive()){
                return true;
            }
        }
        return false;
    }

    public Enemy getEnemyByName(String name) {
        return enemies.stream()
            .filter(enemy -> enemy.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public void setIsShop(boolean b){
        isShop = b;
    }

    public boolean isShop(){
        return isShop;
    }

    public void setIsBoss(boolean b){
        isBossRoom = b;
    }

    public void setIsStart(boolean b){
        isStart = b;
    }

    public boolean isStart(){
        return isStart;    
    }

}