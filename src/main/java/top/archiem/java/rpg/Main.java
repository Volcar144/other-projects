package top.archiem.java.rpg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;

import top.archiem.java.rpg.types.Room;
import top.archiem.java.rpg.WorldLoader;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Room> map = WorldLoader.loadWorld("map.txt");
        System.out.println(map);
    }



}
