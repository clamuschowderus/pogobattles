package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class DodgeAllHuman implements AttackStrategy {
    private int extraDelay;
    private boolean dodgedSpecial = false;
    private AttackStrategyType type;
    private int expectedMinDefDelay = MIN_DEFENDER_DELAY_REASONABLE;
    public static final int CAST_TIME = 0;

    public static final int SECOND_ATTACK_DELAY = 1000;
    public static final int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;
    public static final int REAL_MIN_DEFENDER_DELAY = 1500;
    public static final int MIN_DEFENDER_DELAY_CAUTIOUS = 1500;
    public static final int MIN_DEFENDER_DELAY_REASONABLE = 1750;
    public static final int MIN_DEFENDER_DELAY_RISKY = 2000;
    public static final int MIN_DEFENDER_DELAY_RECKLESS = 2250;
    public static final int MIN_DEFENDER_DELAY_DODGE_SPECIALS = 2500;
    public static final int CHARGE_REALIZATION_DELAY = 700; //Human reaction + time to swipe to dodge.
    public static final int HUMAN_REACTION_TIME = 250; //Average Human reaction time to visual stimulus.
    
    public int timeElapsed = Formulas.START_COMBAT_TIME; //used for the beginning of the battle only.
    
    public AttackStrategyType getType() {
        return type;
    }
    
    public DodgeAllHuman(int extraDelay, AttackStrategyType type, int expectedDefenderDelay){
        this.extraDelay = extraDelay;
        this.type = type;
        expectedMinDefDelay = expectedDefenderDelay;
        dodgedSpecial = false;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
      
        int earliestNextDamageTime = calculateEarliestNextDamageTime(attackerState, defenderState);
      
        //System.out.println("Earliest: " + earliestNextDamageTime + ". Next: " + defenderState.getTimeToNextDamage());
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                    && attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
                // we can sneak in a special attack
                dodgedSpecial = false;
                timeElapsed += attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME;
                return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
            } else if (earliestNextDamageTime > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                // we can sneak in a normal attack
                dodgedSpecial = false;
                timeElapsed += attackerState.getPokemon().getQuickMove().getDuration() + extraDelay;
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
                // We here have to define how much time the attacker will wait to dodge.
                // If we're waiting for a quick attack but we get a long windup charge instead,
                //   we can maybe squeeze some quick attacks after realizing we're getting a charge move but before taking damage.
              
                if(defenderState.getNextMove().getEnergyDelta() <= 0){
                  int moveStartTime = defenderState.getTimeToNextDamage() - defenderState.getNextMove().getDamageWindowStart();
                  int realizationTime = moveStartTime + CHARGE_REALIZATION_DELAY;
                  if(realizationTime < 0){
                    realizationTime = 0;
                  }
                  if(defenderState.getTimeToNextDamage() > realizationTime + attackerState.getPokemon().getQuickMove().getDuration()){
                    // we can sneak in a normal attack after realization
                    dodgedSpecial = false;
                    timeElapsed += attackerState.getPokemon().getQuickMove().getDuration() + realizationTime;
                    return new PokemonAttack(attackerState.getPokemon().getQuickMove(), realizationTime);
                  }
                }
              
                // wait and dodge perfect
                int dodgeWait = defenderState.getTimeToNextDamage()==Integer.MAX_VALUE?earliestNextDamageTime:defenderState.getTimeToNextDamage();
                if(dodgeWait > 1000000 || dodgeWait < 0){
                  dodgeWait = 0;
                }

                dodgedSpecial = defenderState.isNextMoveSpecial();
                timeElapsed += Move.DODGE_MOVE.getDuration() + Math.max(0, dodgeWait - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME);
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, dodgeWait - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta() &&
            (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                ||
             dodgedSpecial
                )) { // two conditions for firing specials, either we know it fits or we've just dodged a special.
            dodgedSpecial = false;
            timeElapsed += attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME;
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
        } else {
          if(defenderState.getNumAttacks()<2){ //early battle
            if (earliestNextDamageTime > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
              // we can sneak in a normal attack
              dodgedSpecial = false;
              timeElapsed += attackerState.getPokemon().getQuickMove().getDuration() + extraDelay;
              return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
              dodgedSpecial = false; // early battle defender never throws specials
              timeElapsed += Move.DODGE_MOVE.getDuration() + Math.max(0, earliestNextDamageTime - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME);
              return new PokemonAttack(Move.DODGE_MOVE,
                  Math.max(0, earliestNextDamageTime - Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME));
            }
          }else{
            dodgedSpecial = false;
            timeElapsed += attackerState.getPokemon().getQuickMove().getDuration() + extraDelay;
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
          }
        }
    }

    public int getDelay() {
        return getDelay();
    }
    
    private int calculateEarliestNextDamageTime(CombatantState attackerState, CombatantState defenderState){
      
      int earliestNextDamageTime = -1;
      
      if(defenderState.getNumAttacks() < 3){
        //Beginning of battle
        int firstDamageTime = FIRST_ATTACK_TIME + Formulas.START_COMBAT_TIME + defenderState.getNextMove().getDamageWindowStart();
        int secondDamageTime = firstDamageTime + SECOND_ATTACK_DELAY;
        int thirdDamageTime  = firstDamageTime + defenderState.getNextMove().getDuration() + expectedMinDefDelay;
        
        if(timeElapsed < firstDamageTime){
          earliestNextDamageTime = firstDamageTime - timeElapsed;
        }else if(timeElapsed < secondDamageTime){
          earliestNextDamageTime = secondDamageTime - timeElapsed;
        }else{
          earliestNextDamageTime = thirdDamageTime - timeElapsed;
        }
      }else{
        int chargeWindowStart = defenderState.getPokemon().getChargeMove().getDamageWindowStart();
        int quickWindowStart = defenderState.getPokemon().getQuickMove().getDamageWindowStart();
        
        int shortestWindowStart = chargeWindowStart<quickWindowStart?chargeWindowStart:quickWindowStart;
        
        int expectedDelayToUse = expectedMinDefDelay;
        
        if(expectedMinDefDelay == MIN_DEFENDER_DELAY_DODGE_SPECIALS){
          expectedDelayToUse = REAL_MIN_DEFENDER_DELAY + (chargeWindowStart - shortestWindowStart); //never risk eating a charge attack!!
        }

        if(defenderState.isDodged() || defenderState.getTimeToNextDamage() == Integer.MAX_VALUE){
          // current attack already dodged or damage already taken but next attack hasn't started yet.
          // We have to backtrack the current attack to figure out when the next one can possibly hit.
          
          earliestNextDamageTime = defenderState.getTimeToNextAttack() + expectedDelayToUse + shortestWindowStart;
          
        }else{
          int attackStartTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDuration();
        
          earliestNextDamageTime = attackStartTime + shortestWindowStart - (defenderState.getNextAttack().getDelay() - expectedDelayToUse);
        }
        
      }
      
//      if(earliestNextDamageTime < 0){
//        System.out.println("Negative earliestNextDamage: " + earliestNextDamageTime);
//        earliestNextDamageTime = 0;
//      }
      
      return earliestNextDamageTime;

    }

}
