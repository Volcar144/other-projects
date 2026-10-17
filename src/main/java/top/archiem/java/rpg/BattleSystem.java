package top.archiem.java.rpg;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import top.archiem.java.rpg.types.Enemy;
import top.archiem.java.rpg.types.Item;
import top.archiem.java.rpg.types.ItemTypes;
import top.archiem.java.rpg.types.Player;
import top.archiem.java.rpg.types.Room;
import top.archiem.java.rpg.helpers.ConsoleTyper;

public class BattleSystem {

    private static void narrate(ConsoleTyper tp, String text) {
        tp.setSpeed(ConsoleTyper.Speed.FAST);
        tp.println(text);
    }

    private static void msg(ConsoleTyper tp, String text) {
        tp.setSpeed(ConsoleTyper.Speed.INSTANT);
        tp.println(text);
    }

    private static void say(ConsoleTyper tp, String text) {
        tp.setSpeed(ConsoleTyper.Speed.INSTANT);
        tp.print(text);
    }

    public static boolean runBattle(Room room, Scanner input, Player player, ConsoleTyper tp) {
        ArrayList<Enemy> enemies = room.getEnemies();
        narrate(tp, AnsiColors.red(AnsiColors.bold("A battle has started!")));
        narrate(tp, "You are fighting: ");
        for (Enemy e : enemies) {
            narrate(tp, AnsiColors.yellow(e.getName()));
        }
        while (room.hasAliveEnemies()) {
            if (player.getHp() == 0) {
                return false;
            }
            msg(tp, "Your health: ");
            msg(tp, player.hpBar());
            msg(tp, AnsiColors.yellow("It is your turn, pick an action: "));
            msg(tp, AnsiColors.cyan("[1] Attack [2] Heavy Strike [3] Use Potion [4] Flee"));
            int action = 0;
            while (action == 0) {
                try {
                    action = Integer.parseInt(input.next());
                } catch (Exception e) {
                    msg(tp, AnsiColors.red("Pick an action: "));
                }
            }
            switch (action) {
                case 1 -> {
                    msg(tp, AnsiColors.cyan("Enemies: "));
                    for (Enemy e : enemies) {
                        int index = enemies.indexOf(e) + 1;
                        say(tp, AnsiColors.yellow("[" + index + "] ") + AnsiColors.red(e.getName()) + " ");
                    }
                    msg(tp, " ");

                    int index = 0;
                    while (index == 0 || index < 1 || index > enemies.size() || enemies.get(index - 1) == null) {
                        msg(tp, AnsiColors.blue("Choose an enemy to fight: "));
                        try {
                            index = Integer.parseInt(input.next());
                        } catch (Exception e) {
                            msg(tp, AnsiColors.red("Not an enemy."));
                        }
                    }
                    int chosen = index - 1;
                    Enemy toFight = enemies.get(chosen);

                    int damage = rollDamage(player.getAttack()).intValue();
                    int dealt = toFight.takeDamage(damage);
                    narrate(tp, AnsiColors.green("You dealt: " + dealt + " damage"));
                    narrate(tp, AnsiColors.yellow("Enemy: "));
                    msg(tp, toFight.hpBar());
                    if (!toFight.isAlive()) {
                        narrate(tp, AnsiColors.green("You have defeated " + AnsiColors.bold(toFight.getName())));
                        Item loot = toFight.rollLoot();
                        player.addGold(toFight.getGoldReward());
                        player.addXp(toFight.getXpReward());
                        boolean isLoot = loot == null;
                        String itemString = isLoot ? AnsiColors.red("nothing") : loot.describe();
                        if (!isLoot) {
                            player.pickupItem(loot);
                        }

                        narrate(tp, AnsiColors.green("You got " + toFight.getGoldReward() + " gold and "
                                + toFight.getXpReward() + " xp. You also found " + itemString));
                        enemies.remove(toFight);
                    }
                }
                case 2 -> {
                    msg(tp, AnsiColors.cyan("Enemies: "));
                    for (Enemy e : enemies) {
                        int index = enemies.indexOf(e) + 1;
                        say(tp, AnsiColors.yellow("[" + index + "] ") + AnsiColors.red(e.getName()) + " ");
                    }
                    msg(tp, " ");

                    int index = 0;
                    while (index == 0 || index < 1 || index > enemies.size() || enemies.get(index - 1) == null) {
                        msg(tp, AnsiColors.blue("Choose an enemy to fight: "));
                        try {
                            index = Integer.parseInt(input.next());
                        } catch (Exception e) {
                            msg(tp, AnsiColors.red("Not an enemy."));
                        }
                    }
                    int chosen = index - 1;
                    Enemy toFight = enemies.get(chosen);
                    int preDamage = rollDamage(player.getAttack()).intValue();
                    Long rounded = Math.round(preDamage * 1.5);

                    int damage = rounded.intValue();
                    int taken = damage / 10;

                    int dealt = toFight.takeDamage(damage);
                    int doneTook = player.takeDamage(taken);
                    narrate(tp, AnsiColors.green("You dealt: " + dealt + " damage but took " + doneTook + " damage"));
                    msg(tp, toFight.hpBar());
                    if (!toFight.isAlive()) {
                        narrate(tp, AnsiColors.green("You have defeated " + AnsiColors.bold(toFight.getName())));
                        Item loot = toFight.rollLoot();
                        player.addGold(toFight.getGoldReward());
                        player.addXp(toFight.getXpReward());
                        boolean isLoot = loot == null;
                        String itemString = isLoot ? AnsiColors.red("nothing") : loot.describe();

                        narrate(tp, AnsiColors.green("You got " + toFight.getGoldReward() + " gold and "
                                + toFight.getXpReward() + " xp. You also found " + itemString));
                        enemies.remove(toFight);
                    }
                }
                case 3 -> {
                    msg(tp, AnsiColors.cyan("Potions: "));
                    boolean hasType = player.getInventory().stream()
                            .anyMatch(item -> item.getItemType() == ItemTypes.POTION);
                    if (hasType) {
                        for (Item i : player.getInventory()) {
                            if (i.getItemType() == ItemTypes.POTION) {
                                int index = player.getInventory().indexOf(i) + 1;
                                say(tp, AnsiColors.yellow("[" + index + "] ") + AnsiColors.bold(i.getName()) + " ");
                            }
                        }

                        msg(tp, " ");
                        int index = 0;
                        while (index == 0 || index > player.getInventory().size()
                                || player.getInventory().get(index - 1) == null) {
                            msg(tp, AnsiColors.blue("What potion do you want to take: "));
                            try {
                                index = Integer.parseInt(input.next());
                            } catch (Exception e) {
                                msg(tp, AnsiColors.red("Please pick a valid potion."));
                            }
                        }
                        int potionNum = index - 1;
                        player.usePotion(player.getInventory().get(potionNum));
                    } else {
                        msg(tp, AnsiColors.red("You have no potions in your inventory"));
                    }
                }
                case 4 -> {
                    if (!room.isBossRoom()) {
                        Random r = new Random();
                        double random = r.nextDouble(0.0, 1.0);
                        if (random <= 0.4) {
                            narrate(tp, AnsiColors.green("You have escaped successfully, but they'll be ready..."));
                            return true;
                        } else {
                            int enemyIndex = r.nextInt(0, enemies.size());
                            Enemy enemy = enemies.get(enemyIndex);
                            Long preDamage = rollDamage(enemy.getAttack());
                            int damage = preDamage.intValue();

                            int taken = player.takeDamage(damage);
                            narrate(tp, AnsiColors.yellow("You escaped but took " + taken + " damage"));
                            return true;
                        }
                    } else {
                        msg(tp, AnsiColors.red("You cannot flee from a boss battle!"));
                    }
                }
            }
            narrate(tp, AnsiColors.magenta("=== Enemy's turn ==="));
            for (Enemy e : enemies) {
                narrate(tp, AnsiColors.red(e.getName() + "'s turn"));
                Long preDamage = rollDamage(e.getAttack());
                int damage = preDamage.intValue();

                int taken = player.takeDamage(damage);
                narrate(tp, AnsiColors.red(e.getName() + " dealt you " + taken + " damage."));
                if (player.getHp() == 0) {
                    return false;
                }
            }
            if (player.getHp() == 0) {
                return false;
            }
        }
        return false;
    }

    private static Long rollDamage(int damage) {
        Random r = new Random();

        double multiplier = Enemy.round(r.nextDouble(0.70, 1.50), 2);
        return Math.round(multiplier * damage);
    }
}