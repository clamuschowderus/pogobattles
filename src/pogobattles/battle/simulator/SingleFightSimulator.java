package pogobattles.battle.simulator;

import java.text.MessageFormat;
import java.util.Hashtable;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.strategies.AttackStrategy;
import pogobattles.battle.strategies.AttackStrategy.PokemonAttack;
import pogobattles.battle.strategies.AttackStrategyRegistry;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;

public class SingleFightSimulator implements FightSimulator {
    private GameMaster gameMaster;
    private PokemonDataCreator creator;
    private Hashtable<Integer, Move> moveRepository;
    private Hashtable<Integer, Double> cpmRepository;
    private AttackStrategyRegistry attackStrategies = new AttackStrategyRegistry();

    public static final double MAX_POWER = 10.0;
    public static final double MIN_POWER = -10.0;
    
    public SingleFightSimulator(GameMaster gameMaster){
      this.gameMaster = gameMaster;
      moveRepository = gameMaster.getMoveTable();
      cpmRepository = gameMaster.getCpMultiplierTable();
      creator = new PokemonDataCreator(gameMaster);
      attackStrategies.init();
    }
    
    /* (non-Javadoc)
     * @see pogobattles.battle.simulator.FightSimulator#calculateMaxAttackDPS(pogobattles.gamemaster.BasePokemon, pogobattles.gamemaster.BasePokemon, pogobattles.gamemaster.Move, pogobattles.gamemaster.Move, pogobattles.battle.strategies.AttackStrategyType)
     */
    @Override
    public FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1,
            Move move2, AttackStrategyType strategyType) {
        Integer level = (cpmRepository.size()+1)*5;
        System.out.println(level);
        return calculateMaxAttackDPS(attackerBase, defenderBase, move1, move2, strategyType, level, level);

    }

    /* (non-Javadoc)
     * @see pogobattles.battle.simulator.FightSimulator#calculateMaxAttackDPS(pogobattles.gamemaster.BasePokemon, pogobattles.gamemaster.BasePokemon, pogobattles.gamemaster.Move, pogobattles.gamemaster.Move, pogobattles.battle.strategies.AttackStrategyType, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public FightResult calculateMaxAttackDPS(BasePokemon attackerBase, BasePokemon defenderBase, Move move1,
            Move move2, AttackStrategyType strategyType, Integer attackerLevel, Integer defenderLevel) {
        Pokemon attacker = creator.createMaxStatPokemon(attackerBase, attackerLevel, move1, move2);
        Pokemon defender = creator.createMaxStatPokemon(defenderBase, defenderLevel, moveRepository.get(defenderBase.getQuickMoves()[0]),
                moveRepository.get(defenderBase.getChargeMoves()[0]));
        return calculateAttackDPS(attacker, defender, strategyType);
    }

    /* (non-Javadoc)
     * @see pogobattles.battle.simulator.FightSimulator#calculateAttackDPS(pogobattles.battle.data.Pokemon, pogobattles.battle.data.Pokemon, pogobattles.battle.strategies.AttackStrategyType)
     */
    @Override
    public FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender,
            AttackStrategyType attackerStrategy) {
        return calculateAttackDPS(attacker, defender, attackerStrategy, AttackStrategyType.DEFENSE);
    }

    /* (non-Javadoc)
     * @see pogobattles.battle.simulator.FightSimulator#calculateAttackDPS(pogobattles.battle.data.Pokemon, pogobattles.battle.data.Pokemon, pogobattles.battle.strategies.AttackStrategyType, pogobattles.battle.strategies.AttackStrategyType)
     */
    @Override
    public FightResult calculateAttackDPS(Pokemon attacker, Pokemon defender,
            AttackStrategyType attackerStrategy, AttackStrategyType defenseStrategy) {
        Fight fight = new Fight(attacker, defender, attackerStrategy, defenseStrategy);
        return fight(fight, true);
    }

    private void nextAttack(AttackStrategy strategy, CombatantState attackerState, CombatantState defenderState) {
        PokemonAttack nextAttack = strategy.nextAttack(attackerState, defenderState);
        attackerState.setNextAttack(nextAttack, nextAttack.getMove());
    }
    boolean isDefender(AttackStrategy strategy) {
        return strategy.getType().name().startsWith("DEFENSE");
    }

    /* (non-Javadoc)
     * @see pogobattles.battle.simulator.FightSimulator#fight(pogobattles.battle.simulator.Fight, boolean)
     */
    @Override
    public FightResult fight(Fight fight, boolean includeDetails) {
        Pokemon attacker = fight.getAttacker();
        Pokemon defender = fight.getDefender();
        //System.out.println(MessageFormat.format("A{0}: CP {1}, {2}, {3}", new Object[]{attacker.getBasePokemon().getName(), attacker.getCp(), attacker.getQuickMove().getName(), attacker.getChargeMove().getName()}));
        //System.out.println(MessageFormat.format("D{0}: CP {1}, {2}, {3}", new Object[]{defender.getBasePokemon().getName(), defender.getCp(), defender.getQuickMove().getName(), defender.getChargeMove().getName()}));
        BasePokemon a = attacker.getBasePokemon();
        BasePokemon d = defender.getBasePokemon();
        // handle ditto
        if (attacker.getQuickMove().getName().equals("TRANSFORM")) {
            //a = pokemonRepository.transform(a,d);
            attacker = creator.transform(attacker, defender);
        }
        if (defender.getQuickMove().getName().equals("TRANSFORM")) {
            //d = pokemonRepository.transform(d,a);
            defender = creator.transform(defender, attacker);
        }
        
        AttackStrategy attackerStrategy = attackStrategies.create(fight.getStrategy());
        AttackStrategy defenderStrategy = attackStrategies.create(fight.getDefenseStrategy());
        FightResult fightResult = new FightResult();
        
        CombatantState attackerState = new CombatantState(attacker, isDefender(attackerStrategy));
        CombatantState defenderState = new CombatantState(defender, isDefender(defenderStrategy));
        
        
        nextAttack(defenderStrategy, defenderState, attackerState);
        int currentTime = Formulas.START_COMBAT_TIME;
        //log.debug("{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), defenderState.getNextMove().getMoveId(), defenderState.getCurrentEnergy());
        //System.out.println(MessageFormat.format("{0}: {1} chose {2} with {3} energy", new Object[]{(currentTime - Formulas.START_COMBAT_TIME)+"", ""+defenderState.getPokemon().getBasePokemon().getName(), ""+defenderState.getNextMove().getName(), ""+defenderState.getCurrentEnergy()}));

        while (attackerState.isAlive() && defenderState.isAlive() && currentTime < Formulas.MAX_COMBAT_TIME_MS) {
            // do defender first since defender strategy determines attacker
            // strategy
            if (defenderState.getNextMove() == null) {
                nextAttack(defenderStrategy, defenderState, attackerState);
                //log.debug("D{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), defenderState.getNextMove().getMoveId(), defenderState.getCurrentEnergy());
                //System.out.println(MessageFormat.format("D{0}: {1} chose {2} with {3} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon().getBasePokemon().getName(), defenderState.getNextMove().getName(), defenderState.getCurrentEnergy()}));
            }
            if (attackerState.getNextMove() == null) {
                nextAttack(attackerStrategy, attackerState, defenderState);
                //log.debug("A{}: {} chose {} with {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), attackerState.getNextMove().getMoveId(), attackerState.getCurrentEnergy());
                //System.out.println(MessageFormat.format("A{0}: {1} chose {2} with {3} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon().getBasePokemon().getName(), attackerState.getNextMove().getName(), attackerState.getCurrentEnergy()}));
            }
            int timeToNextAttack = attackerState.getTimeToNextAttack();
            int timeToNextDefense = defenderState.getTimeToNextAttack();
            int timeToNextAttackDamage = attackerState.getTimeToNextDamage();
            int timeToNextDefenseDamage = defenderState.getTimeToNextDamage();
            
            // make sure we arent over the max time
            if ((currentTime + timeToNextAttack > Formulas.MAX_COMBAT_TIME_MS ||
                currentTime + timeToNextAttackDamage > Formulas.MAX_COMBAT_TIME_MS) &&
                (currentTime + timeToNextDefense > Formulas.MAX_COMBAT_TIME_MS &&                    
                currentTime + timeToNextDefenseDamage > Formulas.MAX_COMBAT_TIME_MS)) {
                currentTime = Formulas.MAX_COMBAT_TIME_MS;
            }
            // tie goes to attacker
            else if (timeToNextAttackDamage >= 0 && timeToNextAttackDamage <= timeToNextAttack &&
                    timeToNextAttackDamage <= timeToNextDefense && timeToNextAttackDamage <= timeToNextDefenseDamage ) {
                CombatResult result = Formulas.getCombatResult(gameMaster, attackerState.getAttack(),
                        defenderState.getDefense(), attackerState.getNextMove(), a, d,  attackerState.isDodged());
                currentTime += timeToNextAttackDamage;
                result.setCurrentTime(currentTime);
                result.setAttacker(attacker);
                result.setDefender(defender);

                attackerState.applyAttack(result, timeToNextAttackDamage);
                int energyGain = defenderState.applyDefense(result, timeToNextAttackDamage);
                //log.debug("A{}: {} took {} damage and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), 
                //        result.getDamage(), energyGain);
                //System.out.println(MessageFormat.format("A{0}: {1} took {2} damage and gained {3} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon().getBasePokemon().getName(), 
                //            result.getDamage(), energyGain}));
                if (includeDetails) {
                	fightResult.addCombatResult(result);
                }

            } else if (timeToNextDefenseDamage >= 0 && timeToNextDefenseDamage <= timeToNextAttack &&
                    timeToNextDefenseDamage <= timeToNextDefense) {
                CombatResult result = Formulas.getCombatResult(gameMaster, defenderState.getAttack(),
                        attackerState.getDefense(), defenderState.getNextMove(), d, a, defenderState.isDodged());
                currentTime += timeToNextDefenseDamage;
               
                result.setCurrentTime(currentTime);
                result.setAttacker(defender);
                result.setDefender(attacker);
                defenderState.applyAttack(result, timeToNextDefenseDamage);
                int energyGain = attackerState.applyDefense(result, timeToNextDefenseDamage);
                //log.debug("D{}: {} took {} damage and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), 
                //        result.getDamage(), energyGain);
                // log.debug("Defender State {}",defenderState);
                //System.out.println(MessageFormat.format("D{0}: {1} took {2} damage and gained {3} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon().getBasePokemon().getName(), 
                //            result.getDamage(), energyGain}));
                if (includeDetails) {
                	fightResult.addCombatResult(result);
                }
            } else if (timeToNextAttack <= timeToNextDefense) {
                currentTime += timeToNextAttack;
                int energyGain = attackerState.resetAttack(timeToNextAttack);
                //log.debug("A{}: {} finished his attack and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon(), 
                //         energyGain);
                //System.out.println(MessageFormat.format("A{0}: {1} finished his attack and gained {2} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, attackerState.getPokemon().getBasePokemon().getName(), 
                //             energyGain}));
                defenderState.moveTime(timeToNextAttack);
            } else {
                currentTime += timeToNextDefense;
                int energyGain = defenderState.resetAttack(timeToNextDefense);
                //log.debug("D{}: {} finished his attack and gained {} energy", currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon(), 
                //        energyGain);
                //System.out.println(MessageFormat.format("D{0}: {1} finished his attack and gained {2} energy", new Object[]{currentTime - Formulas.START_COMBAT_TIME, defenderState.getPokemon().getBasePokemon().getName(), 
                //            energyGain}));
                attackerState.moveTime(timeToNextDefense);
            }
        }
        int prestige = (defenderState.isAlive())?0:Formulas.defensePrestigeGain(attacker.getCp(), defender.getCp());
        fightResult.setWin(!defenderState.isAlive());
        fightResult.setTotalCombatTime(currentTime);
        fightResult.setPrestige(prestige);
        fightResult.addCombatant(attackerState.toResult(attackerStrategy.getType(), currentTime));
        fightResult.addCombatant(defenderState.toResult(defenderStrategy.getType(), currentTime));
        fightResult.setFightParameters(fight);
        fightResult.setPowerLog(getPower(fightResult));
        fightResult.setPower(Math.pow(10, fightResult.getPowerLog()));
        return fightResult;
    }
    double getPower(FightResult result) {
        CombatantResult attacker = result.getCombatant(0);
        CombatantResult defender = result.getCombatant(1);
        double attackerPower =  Math.min(MAX_POWER, (attacker.getStartHp() - attacker.getEndHp()) / (double) attacker.getStartHp());
        double defenderPower =  Math.min(MAX_POWER, (defender.getStartHp() - defender.getEndHp()) / (double) defender.getStartHp());
        // if we return a log, we can add and the numbers stay much smaller!
        if (attackerPower == 0.0) {
            // attacker takes no damage
            return MAX_POWER;
        } else if (defenderPower == 0.0) {
            // defender takes no damage
            return MIN_POWER;
        } else {
            return  Math.max(MIN_POWER, Math.min(MAX_POWER,Math.log10(defenderPower/attackerPower)));
        }
    }

    public GameMaster getGameMaster() {
      return gameMaster;
    }

    public void setGameMaster(GameMaster gameMaster) {
      this.gameMaster = gameMaster;
    }

    public PokemonDataCreator getCreator() {
      return creator;
    }

    public void setCreator(PokemonDataCreator creator) {
      this.creator = creator;
    }

    public Hashtable<Integer, Move> getMoveRepository() {
      return moveRepository;
    }

    public void setMoveRepository(Hashtable<Integer, Move> moveRepository) {
      this.moveRepository = moveRepository;
    }

    public Hashtable<Integer, Double> getCpmRepository() {
      return cpmRepository;
    }

    public void setCpmRepository(Hashtable<Integer, Double> cpmRepository) {
      this.cpmRepository = cpmRepository;
    }

    public AttackStrategyRegistry getAttackStrategies() {
      return attackStrategies;
    }

    public void setAttackStrategies(AttackStrategyRegistry attackStrategies) {
      this.attackStrategies = attackStrategies;
    }

}
