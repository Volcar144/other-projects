package top.archiem.java.rpg.types;

public abstract class Entity {
    String name;

    int hp;
    int maxHp;
    int defense;
    int attack;

    public int takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        hp -= actualDamage;
        if (hp < 0) {
            hp = 0;
        }
        return actualDamage;
    }

    public void heal(int amount){
        int tempHealth = hp + amount;
        if(tempHealth > maxHp){
            hp = maxHp;
            return;
        }
        hp = tempHealth;
    }

    public int getAttack(){
        return attack;
    }

    public boolean isAlive(){
        if(hp == 0){
            return false;
        } else{
            return true;
        }
    }

    public String hpBar(){
        int filled = (int) Math.round(((double) hp / maxHp) * 20);
        int empty = 20 - filled;
        if(hp == maxHp){
            empty = 0;
        } else if (hp == 0){
            filled = 0;
            empty = 20;
        }

        Character filledChar = '\u2588';
        Character emptyChar = '\u2591';

        String filledPortion = filledChar.toString().repeat(filled);
        String emptyPortion = emptyChar.toString().repeat(empty);

        return "[" + filledPortion + emptyPortion + "] " + String.valueOf(hp) + "/" + String.valueOf(maxHp);
    }
}