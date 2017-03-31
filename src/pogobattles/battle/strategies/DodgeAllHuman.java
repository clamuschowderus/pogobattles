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
    public static final int MIN_DEFENDER_DELAY_CAUTIOUS = 1500;
    public static final int MIN_DEFENDER_DELAY_REASONABLE = 1650;
    public static final int MIN_DEFENDER_DELAY_RISKY = 1800;
    public static final int MIN_DEFENDER_DELAY_RECKLESS = 2000;
    public static final int CHARGE_REALIZATION_DELAY = 700; //Human reaction + time to swipe to dodge.
    
    public AttackStrategyType getType() {
        return type;
    }

    public DodgeAllHuman(int extraDelay, AttackStrategyType type, int expectedDefenderDelay) {
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
            if (defenderState.getTimeToNextDamage() <= Formulas.DODGE_WINDOW + extraDelay) {
                //if getTimeToNextDamage() is less than DODGE_WINDOW, we've already seen the yellow flash, dodge immediately.
                dodgedSpecial = defenderState.isNextMoveSpecial();
                return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                    && attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
                // we can sneak in a special attack
                dodgedSpecial = false;
                return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
            } else if (earliestNextDamageTime > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                // we can sneak in a normal attack
                dodgedSpecial = false;
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
                    return new PokemonAttack(attackerState.getPokemon().getQuickMove(), realizationTime);
                  }
                }
              
                // wait and dodge perfect
                int dodgeWait = defenderState.getTimeToNextDamage()==Integer.MAX_VALUE?earliestNextDamageTime:defenderState.getTimeToNextDamage();
                if(dodgeWait > 1000000 || dodgeWait < 0){
                  dodgeWait = 0;
                }

                dodgedSpecial = defenderState.isNextMoveSpecial();
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, dodgeWait - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta() &&
            (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                ||
             dodgedSpecial
                )) { // two conditions for firing specials, either we know it fits or we've just dodged a special.
            dodgedSpecial = false;
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
        } else {
          if(defenderState.getNumAttacks()<2){ //early battle
            if (earliestNextDamageTime > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
              // we can sneak in a normal attack
              dodgedSpecial = false;
              return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
              dodgedSpecial = false; // early battle defender never throws specials
              return new PokemonAttack(Move.DODGE_MOVE,
                  Math.max(0, earliestNextDamageTime - Formulas.DODGE_WINDOW));
            }
          }else{
            dodgedSpecial = false;
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
          }
        }
    }

    public int getDelay() {
        return getDelay();
    }
    
    private int calculateEarliestNextDamageTime(CombatantState attackerState, CombatantState defenderState){
      
      int earliestNextDamageTime = -1;
      
      if(defenderState.getNumAttacks() == 0){
        //first defender attack
        earliestNextDamageTime = FIRST_ATTACK_TIME;
        //System.out.println("First Attack! " + attackerState.getActualCombatTime());
        if(defenderState.getTimeToNextDamage() == Integer.MAX_VALUE){
          // First Damage already registered but second attack not started yet.
          // We have to backtrack the first attack to figure out when the second will hit, so we can dodge or squeeze in a quick.
          earliestNextDamageTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDuration() + defenderState.getNextMove().getDamageWindowStart() + SECOND_ATTACK_DELAY;
        }
      }else if(defenderState.getNumAttacks() == 1){
        //second defender attack
        //System.out.println("Second Attack!");
        if(defenderState.getTimeToNextDamage() == Integer.MAX_VALUE){
          // Second attack registered but third hasn't.
          // We have to backtrack the first attack to figure out when the second will hit, so we can dodge or squeeze in a quick.
          earliestNextDamageTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDuration() + defenderState.getNextMove().getDamageWindowStart() + SECOND_ATTACK_DELAY;
        }else{
          earliestNextDamageTime = defenderState.getTimeToNextDamage();
        }
      }else if(defenderState.getNumAttacks() == 2){
        //third defender attack
        earliestNextDamageTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDuration() - defenderState.getNextAttack().getDelay() + expectedMinDefDelay;
      }else{
        int chargeWindowStart = defenderState.getPokemon().getChargeMove().getDamageWindowStart();
        int quickWindowStart = defenderState.getPokemon().getQuickMove().getDamageWindowStart();
        
        int shortestWindowStart = chargeWindowStart<quickWindowStart?chargeWindowStart:quickWindowStart;

        if(defenderState.isDodged() || defenderState.getTimeToNextDamage() == Integer.MAX_VALUE){
          // current attack already dodged or damage already taken but next attack hasn't started yet.
          // We have to backtrack the current attack to figure out when the next one can possibly hit.
          
          earliestNextDamageTime = defenderState.getTimeToNextAttack() + expectedMinDefDelay + shortestWindowStart;
          
        }else{
          int attackStartTime = defenderState.getTimeToNextAttack() - defenderState.getNextMove().getDuration();
        
          earliestNextDamageTime = attackStartTime + shortestWindowStart - (defenderState.getNextAttack().getDelay() - expectedMinDefDelay);
        }
        
      }
      
//      if(earliestNextDamageTime < 0){
//        System.out.println("Negative earliestNextDamage: " + earliestNextDamageTime);
//        earliestNextDamageTime = 0;
//      }
      
      return earliestNextDamageTime;

    }

}
