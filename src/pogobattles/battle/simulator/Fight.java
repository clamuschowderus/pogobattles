package pogobattles.battle.simulator;

import java.util.List;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.strategies.AttackStrategyType;

public class Fight {
  private Pokemon attacker;
  private Pokemon defender;
  private AttackStrategyType strategy;
  private AttackStrategyType defenseStrategy;
  //private List<Pokemon> pokemons;
  
  public Fight(Pokemon attacker, Pokemon defender, AttackStrategyType strategyType, AttackStrategyType defenseStrategyType){
    this.attacker = attacker;
    this.defender = defender;
    this.strategy = strategyType;
    this.defenseStrategy = defenseStrategyType;
  }
  
  public Pokemon getAttacker() {
    return attacker;
  }
  public void setAttacker(Pokemon attacker) {
    this.attacker = attacker;
  }
  public Pokemon getDefender() {
    return defender;
  }
  public void setDefender(Pokemon defender) {
    this.defender = defender;
  }
  public AttackStrategyType getStrategy() {
    return strategy;
  }
  public void setStrategy(AttackStrategyType strategy) {
    this.strategy = strategy;
  }
  public AttackStrategyType getDefenseStrategy() {
    return defenseStrategy;
  }
  public void setDefenseStrategy(AttackStrategyType defenseStrategy) {
    this.defenseStrategy = defenseStrategy;
  }
}
