package top.archiem.java.rpg.types;

public abstract class Entity {
    String name;

    int hp;
    int maxHp;
    int defense;
    int attack;

    public void takeDamage(int damage) {
        int actualDamage = Math.max(0, damage - defense);
        hp -= actualDamage;
        if (hp < 0) {
            hp = 0;
        }
    }
}