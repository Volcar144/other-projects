package top.archiem.java.rpg;

import java.util.ArrayList;
import java.util.Scanner;

import top.archiem.java.rpg.types.Enemy;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;

public class BattleSystem{
    
    public static void runBattle(Room room, Scanner input, Player player){
        ArrayList<Enemy> enemies = room.getEnemies();
        System.out.println("A battle has started!");
        while(room.hasAliveEnemies()){
            System.out.println(player.hpBar());
            System.out.println("It is your turn, pick an action: ");
            System.out.println("[1] Attack [2] Heavy Strike [3] Use Potion [4] Flee");
            int action = 0;
            while (action == 0){
                try {
                    action = Integer.parseInt(input.next());
                } catch (Exception e) {
                    System.out.println("Pick an action: ");
                }
            }
            switch(action){
                case 1 -> {
                    System.out.println("Choose an enemy to fight: ");
                    for(Enemy e : enemies){
                        int index = enemies.indexOf(e) + 1;
                        System.out.print("[" + index + "] " + e.getName() + " ");
                    }

                    int index = 0;
                    while (index == 0 || enemies.get(index - 1) == null){
                        try {
                            index = Integer.parseInt(input.next());
                        } catch (Exception e) {
                            System.out.println("Choose an enemy to fight: ");
                        }
                    }
                    
                }
            }
        }
    }


}