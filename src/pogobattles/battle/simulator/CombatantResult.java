package pogobattles.battle.simulator;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.strategies.AttackStrategyType;

public class CombatantResult {
  private AttackStrategyType strategy;
  private int damageDealt;
  private int combatTime;
  private double dps;
  private int startHp;
  private int endHp;
  private int id;
  private int energy;
  private Pokemon pokemon;
  private int cp;
  public AttackStrategyType getStrategy() {
    return strategy;
  }
  public void setStrategy(AttackStrategyType strategy) {
    this.strategy = strategy;
  }
  public int getDamageDealt() {
    return damageDealt;
  }
  public void setDamageDealt(int damageDealt) {
    this.damageDealt = damageDealt;
  }
  public int getCombatTime() {
    return combatTime;
  }
  public void setCombatTime(int combatTime) {
    this.combatTime = combatTime;
  }
  public double getDps() {
    return dps;
  }
  public void setDps(double dps) {
    this.dps = dps;
  }
  public int getStartHp() {
    return startHp;
  }
  public void setStartHp(int startHp) {
    this.startHp = startHp;
  }
  public int getEndHp() {
    return endHp;
  }
  public void setEndHp(int endHp) {
    this.endHp = endHp;
  }
  public int getId() {
    return id;
  }
  public int getEnergy() {
    return energy;
  }
  public void setEnergy(int energy) {
    this.energy = energy;
  }
  public Pokemon getPokemon() {
    return pokemon;
  }
  public void setPokemon(Pokemon pokemon) {
    this.pokemon = pokemon;
  }
  public int getCp() {
    return cp;
  }
  public void setCp(int cp) {
    this.cp = cp;
  }
}
