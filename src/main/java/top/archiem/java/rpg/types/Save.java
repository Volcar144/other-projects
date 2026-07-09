package top.archiem.java.rpg.types;

import java.util.ArrayList;
import java.util.HashMap;

public class Save {

    private HashMap<String, Room> updatedRooms;
    private Player player;
    private ArrayList<ShopItem> shopItems;
    private Room currentRoom;

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Save(HashMap<String, Room> updatedRooms, Player player, ArrayList<ShopItem> shopItems) {
        this.updatedRooms = updatedRooms;
        this.player = player;
        this.shopItems = shopItems;
    }

    public Save(){

    }

    public HashMap<String, Room> getUpdatedRooms() {
        return updatedRooms;
    }

    public void setUpdatedRooms(HashMap<String, Room> updatedRooms) {
        this.updatedRooms = updatedRooms;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<ShopItem> getShopItems() {
        return shopItems;
    }

    public void setShopItems(ArrayList<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }
}