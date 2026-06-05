package top.archiem.java.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import top.archiem.java.rpg.types.Enemy;
import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;

public class GameEngine {
    Player player;
    boolean running = false;


    public void startGameLoop() {

        Scanner keyboard = new Scanner(System.in);

        System.out.println("Welcome to jaRpg, an interactive dungeon crawler game");
        System.out.println("To start, create a name for your player: ");
        String name = keyboard.next();
        player = new Player(name);
        System.out.println("Nice to meet you " + name + ". Let's start your adventure");
        System.out.println("You are just entered a dungeon, you look around and you see ");

       


    }

}