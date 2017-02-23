package pogobattles.battle.simulator;

import pogobattles.battle.data.Pokemon;
import pogobattles.gamemaster.Move;

public class CombatResult {
  private Pokemon attacker;
  private Pokemon defender;
  private int combatTime;
  private int damage;
  private double dodgePercent;
  private Move attackMove;
  private boolean criticalHit;
  private int damageTime;
  private int currentTime;
  public Pokemon getAttacker() {
    return attacker;
  }
  public void setAttacker(Pokemon attacker){
    this.attacker = attacker;
  }
  public Pokemon getDefender() {
    return defender;
  }
  public void setDefender(Pokemon defender){
    this.defender = defender;
  }
  public int getCombatTime() {
    return combatTime;
  }
  public void setCombatTime(int combatTime) {
    this.combatTime = combatTime;
  }
  public int getDamage() {
    return damage;
  }
  public void setDamage(int damage) {
    this.damage = damage;
  }
  public double getDodgePercent() {
    return dodgePercent;
  }
  public void setDodgePercent(double dodgePercent) {
    this.dodgePercent = dodgePercent;
  }
  public Move getAttackMove() {
    return attackMove;
  }
  public void setAttackMove(Move attackMove) {
    this.attackMove = attackMove;
  }
  public boolean isCriticalHit() {
    return criticalHit;
  }
  public void setCriticalHit(boolean criticalHit) {
    this.criticalHit = criticalHit;
  }
  public int getDamageTime() {
    return damageTime;
  }
  public void setDamageTime(int damageTime) {
    this.damageTime = damageTime;
  }
  public int getCurrentTime() {
    return currentTime;
  }
  public void setCurrentTime(int currentTime) {
    this.currentTime = currentTime;
  }
}
