package pogobattles.battle.simulator;

import java.util.ArrayList;
import java.util.List;

public class FightResult {
  private boolean win;
  private int totalCombatTime;
  private List<CombatantResult> combatants = new ArrayList<CombatantResult>();
  private List<CombatResult> combatResults = new ArrayList<CombatResult>();
  private Fight fightParameters;
  private double power;
  private double powerLog;
  private double timePower;
  private double totalPower;
  private int prestige;
  public boolean isWin() {
    return win;
  }
  public void setWin(boolean win) {
    this.win = win;
  }
  public int getTotalCombatTime() {
    return totalCombatTime;
  }
  public void setTotalCombatTime(int totalCombatTime) {
    this.totalCombatTime = totalCombatTime;
  }
  public List<CombatantResult> getCombatants() {
    return combatants;
  }
  public void setCombatants(List<CombatantResult> combatants) {
    this.combatants = combatants;
  }
  public List<CombatResult> getCombatResults() {
    return combatResults;
  }
  public void setCombatResults(List<CombatResult> combatResults) {
    this.combatResults = combatResults;
  }
  public Fight getFightParameters() {
    return fightParameters;
  }
  public void setFightParameters(Fight fightParameters) {
    this.fightParameters = fightParameters;
  }
  public double getPower() {
    return power;
  }
  public void setPower(double power) {
    this.power = power;
  }
  public double getPowerLog() {
    return powerLog;
  }
  public void setPowerLog(double powerLog) {
    this.powerLog = powerLog;
  }
  public int getPrestige() {
    return prestige;
  }
  public void setPrestige(int prestige) {
    this.prestige = prestige;
  }
  public void addCombatResult(CombatResult combatResult){
    combatResults.add(combatResult);
  }
  public void addCombatant(CombatantResult combatantResult){
    combatants.add(combatantResult);
  }
  public CombatantResult getCombatant(int index){
    return combatants.get(index);
  }
  public double getTotalPower() {
	return totalPower;
  }
  public void setTotalPower(double totalPower) {
	this.totalPower = totalPower;
  }
  public void setTimePower(double timePower) {
	this.timePower = timePower;
  }
  public double getTimePower(){
	return timePower;
  }
}
