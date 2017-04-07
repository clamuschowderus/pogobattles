package pogobattles.battle.simulator;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;

public class MonteCarloFightSimulator implements FightSimulator {
  
  private SingleFightSimulator sfSim;
  private int numIterations;
  
  public MonteCarloFightSimulator(GameMaster gameMaster, int numIterations){
    sfSim = new SingleFightSimulator(gameMaster);
    this.numIterations = numIterations;
  }
  
  @Override
  public FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1, Move move2, AttackStrategyType strategyType) {
    Integer level = (sfSim.getCpmRepository().size()+1)*5;
    System.out.println(level);
    return calculateMaxAttackDPS(attackerBase, defenderBase, move1, move2, strategyType, level, level);
  }

  @Override
  public FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1, Move move2, AttackStrategyType strategyType, Integer attackerLevel, Integer defenderLevel) {
    Pokemon attacker = sfSim.getCreator().createMaxStatPokemon(attackerBase, attackerLevel, move1, move2);
    Pokemon defender = sfSim.getCreator().createMaxStatPokemon(defenderBase, defenderLevel, sfSim.getMoveRepository().get(defenderBase.getQuickMoves()[0]),
            sfSim.getMoveRepository().get(defenderBase.getChargeMoves()[0]));
    return calculateAttackDPS(attacker, defender, strategyType);
  }

  @Override
  public FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender, AttackStrategyType attackerStrategy) {
    return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE_RANDOM);
  }

  @Override
  public FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender, AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy) {
    MonteCarloFightResult mcfr = new MonteCarloFightResult();
    mcfr.setWin(true);
    for(int i = 0; i < numIterations; i++){
      FightResult singleFightResult = sfSim.calculateAttackDPS(attacker, defender, attackerStrategy, defenseStrategy);
      mcfr.addFightResult(singleFightResult);
    }
    mcfr.calculateAllResults();
    mcfr.setPowerLog(getPower(mcfr));
    mcfr.setPower(Math.pow(10, mcfr.getPowerLog()));
    return mcfr;
  }

  @Override
  public FightResult fight(Fight fight, boolean includeDetails) {
    // does nothing
    return null;
  }

  double getPower(FightResult result) {
    CombatantResult attacker = result.getCombatant(0);
    CombatantResult defender = result.getCombatant(1);
    double attackerPower =  Math.min(SingleFightSimulator.MAX_POWER, (attacker.getStartHp() - attacker.getEndHp()) / (double) attacker.getStartHp());
    double defenderPower =  Math.min(SingleFightSimulator.MAX_POWER, (defender.getStartHp() - defender.getEndHp()) / (double) defender.getStartHp());
    // if we return a log, we can add and the numbers stay much smaller!
    if (attackerPower == 0.0) {
        // attacker takes no damage
        return SingleFightSimulator.MAX_POWER;
    } else if (defenderPower == 0.0) {
        // defender takes no damage
        return SingleFightSimulator.MIN_POWER;
    } else {
        return  Math.max(SingleFightSimulator.MIN_POWER, Math.min(SingleFightSimulator.MAX_POWER,Math.log10(defenderPower/attackerPower)));
    }
  }
  
  

}
