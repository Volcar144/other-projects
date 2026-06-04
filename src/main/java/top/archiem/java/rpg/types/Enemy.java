package top.archiem.java.rpg.types;

public class Enemy extends Entity{

    String name;

    private int hp;
    private int maxHp;
    private int defense;
    private int attack;
    private int goldReward;
    private int xpReward;

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getXpReward() {
        return xpReward;
    }
}
