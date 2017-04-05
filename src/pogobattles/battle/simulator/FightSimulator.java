package pogobattles.battle.simulator;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.Move;

public interface FightSimulator {

  public abstract FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1,
      Move move2, AttackStrategyType strategyType);

  public abstract FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1,
      Move move2, AttackStrategyType strategyType, Integer attackerLevel, Integer defenderLevel);

  public abstract FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender,
      AttackStrategyType attackerStrategy);

  public abstract FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender,
      AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy);

  public abstract FightResult fight(Fight fight, boolean includeDetails);

}