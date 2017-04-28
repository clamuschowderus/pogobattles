package pogobattles.ranking;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.simulator.Formulas;
import pogobattles.battle.strategies.ChargeAttackStrategyType;
import pogobattles.battle.strategies.DodgeWeave.FireSpecialStrategyType;
import pogobattles.gamemaster.Move;

public class MatchupAnalyzer {
  private Pokemon attacker;
  private Pokemon defender;
  
  public static final int MIN_DEFENDER_DELAY = 1500;
  public static final int HUMAN_REACTION_TIME = 300;
  
  private int specialCushion = 0;
  private int quickToCharge = 0;
  private int quickToQuick = 0;
  private int chargeToQuick = 0;
  private int chargeToCharge = 0;
  
  private int timeToFireQuicks = 0;
  private int timeToFireQuicksAvoidCharge = 0;
  private int timeToFireCharge = 0;
  private int minAfterQuick = 0;
  private int minBeforeCharge = 0;
  
  private ChargeAttackStrategyType chargeStrategy = ChargeAttackStrategyType.ALWAYS_FITS;
  
  private int safestQuickPerWeave = 0;
  private int quickPerWeaveToAvoidCharge = 0;
  
  public MatchupAnalyzer(Pokemon attacker, Pokemon defender){
    this.attacker = attacker;
    this.defender = defender;
    
    analyze();
  }
  
  private void analyze(){
    Move defQuick = defender.getQuickMove();
    Move defCharge = defender.getChargeMove();
    Move attQuick = attacker.getQuickMove();
    Move attCharge = attacker.getChargeMove();
    
    int afterQuick = defQuick.getDuration() - defQuick.getDamageWindowStart();
    int afterCharge = defCharge.getDuration() - defCharge.getDamageWindowStart();
    
    //How much longer we have in our weave cycle when the next defender attack is a charge.
    specialCushion = defCharge.getDamageWindowStart() - defQuick.getDamageWindowStart(); 
    
    quickToCharge = afterQuick + MIN_DEFENDER_DELAY + defCharge.getDamageWindowStart();
    quickToQuick = defQuick.getDuration() + MIN_DEFENDER_DELAY;
    chargeToQuick = afterCharge + MIN_DEFENDER_DELAY + defQuick.getDamageWindowStart();
    chargeToCharge = defCharge.getDuration() + MIN_DEFENDER_DELAY;
    
    minAfterQuick = Math.min(defQuick.getDuration() + MIN_DEFENDER_DELAY, quickToCharge);
    minBeforeCharge = Math.min(defCharge.getDuration() + MIN_DEFENDER_DELAY, quickToCharge);
    
    timeToFireCharge = attCharge.getDuration() + Move.DODGE_MOVE.getDuration() + HUMAN_REACTION_TIME;
    
    if(timeToFireCharge < minAfterQuick + Formulas.DODGE_WINDOW){
      // charge fits anywhere.
      chargeStrategy = ChargeAttackStrategyType.ALWAYS_FITS;
    }else{
      if(timeToFireCharge < quickToCharge + Formulas.DODGE_WINDOW){
        // fits after a quick if the defender will fire a charge;
        chargeStrategy = ChargeAttackStrategyType.FITS_BEFORE_SPECIALS;
      }else{
        if(timeToFireCharge < chargeToCharge + Formulas.DODGE_WINDOW){
          chargeStrategy = ChargeAttackStrategyType.FITS_AFTER_SPECIALS;
        }else{
          chargeStrategy = ChargeAttackStrategyType.NEVER_FITS;
        }
      }
    }
    
    timeToFireQuicks = minAfterQuick - Move.DODGE_MOVE.getDuration();
    
    safestQuickPerWeave = timeToFireQuicks/attQuick.getDuration();
    
    timeToFireQuicksAvoidCharge = minBeforeCharge - Move.DODGE_MOVE.getDuration();
    
    quickPerWeaveToAvoidCharge = timeToFireQuicksAvoidCharge/attQuick.getDuration();
  }
  
  public ChargeAttackStrategyType getChargeStrategy(){
    return chargeStrategy;
  }
  
  public int getSafestQuickPerWeave(){
    return safestQuickPerWeave;
  }
  
  public int getQuickPerWeaveToAvoidCharge(){
    return quickPerWeaveToAvoidCharge;
  }
  
  public int getQuickToQuick(){
    return quickToQuick;
  }
  
  public int getQuickToCharge(){
    return quickToCharge;
  }
  
  public int getChargeToQuick(){
    return chargeToQuick;
  }
  
  public int getChargeToCharge(){
    return chargeToCharge;
  }
  
  public double getQuickToQuickRisk(int quickPerWeave){
    return risk(quickPerWeave, quickToQuick);
  }
  
  public double getQuickToChargeRisk(int quickPerWeave){
    return risk(quickPerWeave, quickToCharge);
  }
  
  public double getChargeToQuickRisk(int quickPerWeave){
    return risk(quickPerWeave, chargeToQuick);
  }
  
  public double getChargeToChargeRisk(int quickPerWeave){
    return risk(quickPerWeave, chargeToCharge);
  }
  
  private double risk(int quickPerWeave, int totalTime){
    int weaveTime = quickPerWeave * attacker.getQuickMove().getDuration() + Move.DODGE_MOVE.getDuration();
    int exceedingTime = weaveTime - totalTime;
    if(exceedingTime < 0){
      exceedingTime = 0;
    }
    return ((double)exceedingTime)/1000.00;
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

public int getSpecialCushion() {
	return specialCushion;
}

public void setSpecialCushion(int specialCushion) {
	this.specialCushion = specialCushion;
}

public int getTimeToFireQuicks() {
	return timeToFireQuicks;
}

public void setTimeToFireQuicks(int timeToFireQuicks) {
	this.timeToFireQuicks = timeToFireQuicks;
}

public int getTimeToFireQuicksAvoidCharge() {
	return timeToFireQuicksAvoidCharge;
}

public void setTimeToFireQuicksAvoidCharge(int timeToFireQuicksAvoidCharge) {
	this.timeToFireQuicksAvoidCharge = timeToFireQuicksAvoidCharge;
}

public int getTimeToFireCharge() {
	return timeToFireCharge;
}

public void setTimeToFireCharge(int timeToFireCharge) {
	this.timeToFireCharge = timeToFireCharge;
}

public int getMinAfterQuick() {
	return minAfterQuick;
}

public void setMinAfterQuick(int minAfterQuick) {
	this.minAfterQuick = minAfterQuick;
}

public int getMinBeforeCharge() {
	return minBeforeCharge;
}

public void setMinBeforeCharge(int minBeforeCharge) {
	this.minBeforeCharge = minBeforeCharge;
}

public void setQuickToCharge(int quickToCharge) {
	this.quickToCharge = quickToCharge;
}

public void setQuickToQuick(int quickToQuick) {
	this.quickToQuick = quickToQuick;
}

public void setChargeToQuick(int chargeToQuick) {
	this.chargeToQuick = chargeToQuick;
}

public void setChargeToCharge(int chargeToCharge) {
	this.chargeToCharge = chargeToCharge;
}

public void setChargeStrategy(ChargeAttackStrategyType chargeStrategy) {
	this.chargeStrategy = chargeStrategy;
}

public void setSafestQuickPerWeave(int safestQuickPerWeave) {
	this.safestQuickPerWeave = safestQuickPerWeave;
}

public void setQuickPerWeaveToAvoidCharge(int quickPerWeaveToAvoidCharge) {
	this.quickPerWeaveToAvoidCharge = quickPerWeaveToAvoidCharge;
}
}
