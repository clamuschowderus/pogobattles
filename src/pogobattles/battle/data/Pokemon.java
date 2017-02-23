package pogobattles.battle.data;

import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.Move;

public class Pokemon {
  
  private BasePokemon basePokemon;
  private double cpMultiplier;
  private Integer level;
  private int ivStamina;
  private int ivAttack;
  private int ivDefense;
  private Move quickMove;
  private Move chargeMove;
  private int cp;
  
  private int calculateCp(){
    int intermediateCp = (int)Math.floor((getAtt() * Math.pow(getDef(), 0.5) * Math.pow(getSta(), 0.5) * Math.pow(cpMultiplier, 2))/10);
    if(intermediateCp < 10){
      intermediateCp = 10;
    }
    return intermediateCp;
  }
  
  private int getAtt(){
    return basePokemon.getBaseAttack() + ivAttack;
  }
  
  private int getDef(){
    return basePokemon.getBaseDefense() + ivDefense;
  }
  
  private int getSta(){
    return basePokemon.getBaseStamina() + ivStamina;
  }
  
  public Integer getLevel() {
    return level;
  }
  public void setLevel(Integer level) {
    this.level = level;
  }
  public void setCp(int cp){ // only ever set CP if you want to override the normal CP calculation (ie. Ditto's Transform)
    this.cp = cp;
  }
  public int getCp() {
    if(cp == 0){
      cp = calculateCp();
    }
    return cp;
  }
  public int getAttack() {
    return basePokemon.getBaseAttack() + ivAttack;
  }
  public int getDefense() {
    return basePokemon.getBaseDefense() + ivDefense;
  }
  public int getStamina() {
    return basePokemon.getBaseStamina() + ivStamina;
  }
  public BasePokemon getBasePokemon() {
    return basePokemon;
  }
  public void setBasePokemon(BasePokemon basePokemon) {
    this.basePokemon = basePokemon;
  }
  public double getCpMultiplier() {
    return cpMultiplier;
  }
  public void setCpMultiplier(double cpMultiplier) {
    this.cpMultiplier = cpMultiplier;
    cp = 0; // reset CP so it gets recalculated
  }
  public int getIvStamina() {
    return ivStamina;
  }
  public void setIvStamina(int ivStamina) {
    this.ivStamina = ivStamina;
  }
  public int getIvAttack() {
    return ivAttack;
  }
  public void setIvAttack(int ivAttack) {
    this.ivAttack = ivAttack;
  }
  public int getIvDefense() {
    return ivDefense;
  }
  public void setIvDefense(int ivDefense) {
    this.ivDefense = ivDefense;
  }
  public Move getQuickMove() {
    return quickMove;
  }
  public void setQuickMove(Move quickMove) {
    this.quickMove = quickMove;
  }
  public Move getChargeMove() {
    return chargeMove;
  }
  public void setChargeMove(Move chargeMove) {
    this.chargeMove = chargeMove;
  }
}
