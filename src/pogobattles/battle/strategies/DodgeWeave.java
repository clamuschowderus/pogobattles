package pogobattles.battle.strategies;

import java.util.Random;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class DodgeWeave implements AttackStrategy {
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
    public static final int MIN_DEFENDER_DELAY_EAT_IT_ALL = 2500;
    public static final int MIN_DEFENDER_DELAY_DODGE_SPECIALS = 10000;
    //public static final int MIN_DEFENDER_DELAY_RANDOM = 0;
    //public static final int MIN_DEFENDER_DELAY_RANDOM_SPECIALS_ONLY = -1;
    public static final int CHARGE_REALIZATION_DELAY = 700; //Human reaction + time to swipe to dodge.
    public static final int HUMAN_REACTION_TIME = 250; //Average Human reaction time to visual stimulus.
    
    public int timeElapsed = Formulas.START_COMBAT_TIME; //used for the beginning of the battle only.
    
    private boolean ninjaDodgeEnabled = false;
    
    private boolean firstAttack = false;
    
    private FireSpecialStrategyType chargeStrategy = null;
    
    private boolean limitWeaveRiskBySpecials = false;
    
    private int specialCushion = 0;
    
    private int quickPerWeave = 0;
    
    //used for random strategy
    //private Random r = new Random();
    //public static final double BASE_DODGE_MISS_RATE = 0.1;
    //public PokemonAttack lastDefenderAttack = null;
    //public int currentDelayToUse = REAL_MIN_DEFENDER_DELAY;

    public AttackStrategyType getType() {
        return type;
    }
    
    public DodgeWeave(int extraDelay, AttackStrategyType type, int expectedDefenderDelay, boolean limitWeaveRiskBySpecials, boolean alternateWeave){
      this.extraDelay = extraDelay;
      this.type = type;
      expectedMinDefDelay = expectedDefenderDelay;
      dodgedSpecial = false;
      ninjaDodgeEnabled = false;
      firstAttack = true;
      chargeStrategy = null;
      this.limitWeaveRiskBySpecials = limitWeaveRiskBySpecials;
    }
    
    public boolean isRandomStrategy(){
      return expectedMinDefDelay <= 0;
    }
    
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
      
        if(firstAttack){
          calculateWeave(attackerState, defenderState);
          firstAttack = false;
        }
      
        int earliestNextDamageTime = calculateEarliestNextDamageTime(attackerState, defenderState);
      
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (earliestNextDamageTime < Formulas.DODGE_WINDOW + HUMAN_REACTION_TIME + extraDelay 
                && ninjaDodgeEnabled && !isRandomStrategy()){ //ninja dodge!
              dodgedSpecial = defenderState.isNextMoveSpecial();
              timeElapsed += Move.DODGE_MOVE.getDuration() + extraDelay;
              return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
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
        if(defenderState.getNumAttacks()<3){ //early battle
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
          if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta() &&
              (earliestNextDamageTime > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                  ||
               dodgedSpecial
                )) { // two conditions for firing specials, either we know it fits or we've just dodged a special.
              dodgedSpecial = false;
              timeElapsed += attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME;
              return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
          } else {
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
    
    public void calculateWeave(CombatantState attackerState, CombatantState defenderState){
      Move defQuick = defenderState.getPokemon().getQuickMove();
      Move defCharge = defenderState.getPokemon().getChargeMove();
      Move attQuick = attackerState.getPokemon().getQuickMove();
      Move attCharge = attackerState.getPokemon().getChargeMove();
      
      int afterQuick = defQuick.getDamageWindowEnd() - defQuick.getDamageWindowStart();
      int afterCharge = defCharge.getDamageWindowEnd() - defCharge.getDamageWindowStart();
      
      int riskTime = expectedMinDefDelay - REAL_MIN_DEFENDER_DELAY; // amount of time we're willing to risk.
      
      //How much longer we have in our weave cycle when the next defender attack is a charge.
      specialCushion = defCharge.getDamageWindowStart() - defQuick.getDamageWindowStart(); 
      
      if(specialCushion < riskTime){ // We're risking more than our charge cushion.
        //risking eating specials without dodging.
        if(limitWeaveRiskBySpecials){ // Take risks as long as it doesn't result in eating a special
          expectedMinDefDelay = Math.max(REAL_MIN_DEFENDER_DELAY, REAL_MIN_DEFENDER_DELAY + specialCushion);
        }
      }
      
      if(specialCushion < 0){
        specialCushion = 0;
      }

      int fromQuickToCharge = afterQuick + defCharge.getDamageWindowStart();
      int fromChargeToQuick = afterCharge + defQuick.getDamageWindowStart();
      
      int minAfterQuick = Math.min(defQuick.getDuration(), fromQuickToCharge);
      
      int timeToFireCharge = attCharge.getDuration() + HUMAN_REACTION_TIME;
      
      if(timeToFireCharge < minAfterQuick + expectedMinDefDelay + Formulas.DODGE_WINDOW){
        // charge fits.
        chargeStrategy = FireSpecialStrategyType.WHEN_FITS;
      }else{
        if(timeToFireCharge < fromQuickToCharge + expectedMinDefDelay + Formulas.DODGE_WINDOW){
          // fits after a quick if the defender will fire a charge;
          chargeStrategy = FireSpecialStrategyType.AFTER_QUICK;
          // after quick strategy will use special cushion to determine if special can be fired.
        }else{
          // fire only after defender specials;
          chargeStrategy = FireSpecialStrategyType.AFTER_SPECIAL;
        }
      }
      
      int timeToFireQuicks = expectedMinDefDelay + minAfterQuick - Move.DODGE_MOVE.getDuration();
      
      quickPerWeave = timeToFireQuicks/attQuick.getDuration();    
    }
    
    public enum FireSpecialStrategyType{
      WHEN_FITS,
      AFTER_QUICK,
      AFTER_SPECIAL
    }

}
