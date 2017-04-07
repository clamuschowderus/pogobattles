package pogobattles.battle.simulator;

import java.util.ArrayList;
import java.util.List;

import pogobattles.battle.data.Pokemon;

public class MonteCarloFightResult extends FightResult {
  
  private double winRatio;
  private double timeoutRatio;
  private List<CombatResult> worstCombatResults;

  private List<FightResult> singleFightResults = new ArrayList<FightResult>();
  
  @Override
  public boolean isWin() {
    return super.isWin();
  }

  @Override
  public void setWin(boolean win) {
    super.setWin(win);
  }

  @Override
  public int getTotalCombatTime() {
    return super.getTotalCombatTime();
  }

  @Override
  public void setTotalCombatTime(int totalCombatTime) {
    super.setTotalCombatTime(totalCombatTime);
  }

  @Override
  public List<CombatantResult> getCombatants() {
    return super.getCombatants();
  }

  @Override
  public void setCombatants(List<CombatantResult> combatants) {
    super.setCombatants(combatants);
  }

  @Override
  public List<CombatResult> getCombatResults() {
    return super.getCombatResults();
  }

  @Override
  public void setCombatResults(List<CombatResult> combatResults) {
    super.setCombatResults(combatResults);
  }

  @Override
  public Fight getFightParameters() {
    return super.getFightParameters();
  }

  @Override
  public void setFightParameters(Fight fightParameters) {
    super.setFightParameters(fightParameters);
  }

  @Override
  public double getPower() {
    return super.getPower();
  }

  @Override
  public void setPower(double power) {
    super.setPower(power);
  }

  @Override
  public double getPowerLog() {
    return super.getPowerLog();
  }

  @Override
  public void setPowerLog(double powerLog) {
    super.setPowerLog(powerLog);
  }

  @Override
  public int getPrestige() {
    return super.getPrestige();
  }

  @Override
  public void setPrestige(int prestige) {
    super.setPrestige(prestige);
  }

  @Override
  public void addCombatResult(CombatResult combatResult) {
    super.addCombatResult(combatResult);
  }

  @Override
  public void addCombatant(CombatantResult combatantResult) {
    super.addCombatant(combatantResult);
  }

  @Override
  public CombatantResult getCombatant(int index) {
    return super.getCombatant(index);
  }

  public void addFightResult(FightResult fightResult){
    singleFightResults.add(fightResult);
  }
  
  public double getWinRatio(){
    return winRatio;
  }
  
  public void setWinRatio(double winRatio){
    this.winRatio = winRatio;
  }
  
  public double getTimeoutRatio(){
    return timeoutRatio;
  }
  
  public void setTimeoutRatio(double timeoutRatio){
    this.timeoutRatio = timeoutRatio;
  }
  
  public List<CombatResult> getWorstCombatResults(){
    return worstCombatResults;
  }
  
  public void setWorstCombatResults(List<CombatResult> worstCombatResults){
    this.worstCombatResults = worstCombatResults;
  }
  
  public void calculateAllResults(){
    FightResult fr = singleFightResults.get(0);
    CombatantResult attackerResult = fr.getCombatant(0);
    CombatantResult defenderResult = fr.getCombatant(1);
    Pokemon attacker = attackerResult.getPokemon();
    Pokemon defender = defenderResult.getPokemon();
    CombatantResult finalAttackerResult = new CombatantResult();
    CombatantResult finalDefenderResult = new CombatantResult();
    finalAttackerResult.setCp(attackerResult.getCp());
    finalAttackerResult.setStartHp(attackerResult.getStartHp());
    finalAttackerResult.setPokemon(attackerResult.getPokemon());
    finalAttackerResult.setStrategy(attackerResult.getStrategy());
    finalDefenderResult.setCp(defenderResult.getCp());
    finalDefenderResult.setStartHp(defenderResult.getStartHp());
    finalDefenderResult.setPokemon(defenderResult.getPokemon());
    finalDefenderResult.setStrategy(defenderResult.getStrategy());
    super.setPrestige(Formulas.defensePrestigeGain(attacker.getCp(), defender.getCp()));

    //fightResult.setFightParameters(fight); //not needed
    
    super.setWin(true);
    int numFights = singleFightResults.size();
    long sumTotalCombatTime = 0;
    double worstCombatResultPower = -1;
    double bestCombatResultPower = -1;
    long totalAttEndHp = 0;
    long totalAttDmgDealt = 0;
    long totalDefEndHp = 0;
    long totalDefDmgDealt = 0;
    int numWins = 0;
    int numTimeouts = 0;
    List<CombatResult> worstCombatResults = new ArrayList<CombatResult>();
    List<CombatResult> bestCombatResults = new ArrayList<CombatResult>();
    
    for(FightResult sfr : singleFightResults){
      if(sfr.getTotalCombatTime() < 0){
        //discard invalid sim. There's a bug with Integer.MAX_VALUE used by the FightSimulator.
        numFights--;
        if(numFights == 0){
          numFights = 1; //get at least one entry
        }else{
          continue;
        }
      }
      CombatantResult sfrAttRes = sfr.getCombatant(0);
      CombatantResult sfrDefRes = sfr.getCombatant(1);
      totalAttEndHp += sfrAttRes.getEndHp();
      totalAttDmgDealt += sfrAttRes.getDamageDealt();
      totalDefEndHp += sfrDefRes.getEndHp();
      totalDefDmgDealt += sfrDefRes.getDamageDealt();
      if(!sfr.isWin()){
        super.setWin(false);
      }else{
        numWins++;
      }
      if(sfrAttRes.getEndHp() > 0 && sfrDefRes.getEndHp() > 0){
        numTimeouts++;
      }
      sumTotalCombatTime += (long)sfr.getTotalCombatTime();
      if(sfr.getPower() < worstCombatResultPower || worstCombatResultPower == -1){
        worstCombatResultPower = sfr.getPower();
        worstCombatResults = sfr.getCombatResults();
      }
      if(sfr.getPower() > bestCombatResultPower || bestCombatResultPower == -1){
        bestCombatResultPower = sfr.getPower();
        bestCombatResults = sfr.getCombatResults();
      }
    }
    if(numFights == 0){
      //invalid sim
    }else{
      super.setCombatResults(bestCombatResults); //use the best outcome as default
      setWorstCombatResults(worstCombatResults);
      super.setTotalCombatTime((int)(sumTotalCombatTime/numFights)); //average combat time
      finalAttackerResult.setCombatTime(super.getTotalCombatTime());
      finalDefenderResult.setCombatTime(super.getTotalCombatTime());
      finalAttackerResult.setEndHp((int)(totalAttEndHp / numFights));
      finalDefenderResult.setEndHp((int)(totalDefEndHp / numFights));
      finalAttackerResult.setDamageDealt((int)totalAttDmgDealt / numFights);
      finalDefenderResult.setDamageDealt((int)totalDefDmgDealt / numFights);
      finalAttackerResult.setDps(1000.0f * (finalAttackerResult.getDamageDealt()) / super.getTotalCombatTime());
      finalDefenderResult.setDps(1000.0f * (finalDefenderResult.getDamageDealt()) / super.getTotalCombatTime());
      setWinRatio(((double)numWins)/numFights);
      setTimeoutRatio(((double)numTimeouts)/numFights);
    }
    
    super.addCombatant(finalAttackerResult);
    super.addCombatant(finalDefenderResult);

  }
  
}
