package pogobattles.gamemaster;

public class Move {
  
  public static final int DODGE_ID = -1;
  public static final String DODGE_NAME = "DODGE";
  public static final Move DODGE_MOVE = new Move(DODGE_ID, DODGE_NAME, 0, 500); // default to 500 (GameMaster Parser overrides this value)
  
  private String name;
  private double accuracyChance;
  private double criticalChance;
  private int damageWindowStart;
  private int damageWindowEnd;
  private int duration;
  private int energyDelta;
  private int moveId;
  private int type;
  private int damage;

  public Move(){
  }
  
  private Move(int moveId, String name, int damageWindowStart, int duration){
    this.moveId = moveId;
    this.name = name;
    this.damageWindowStart = damageWindowStart;
    this.duration = duration;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public double getAccuracyChance() {
    return accuracyChance;
  }
  public void setAccuracyChance(double accuracyChance) {
    this.accuracyChance = accuracyChance;
  }
  public double getCriticalChance() {
    return criticalChance;
  }
  public void setCriticalChance(double criticalChance) {
    this.criticalChance = criticalChance;
  }
  public int getDamageWindowStart() {
    return damageWindowStart;
  }
  public void setDamageWindowStart(int damageWindowStart) {
    this.damageWindowStart = damageWindowStart;
  }
  public int getDamageWindowEnd() {
    return damageWindowEnd;
  }
  public void setDamageWindowEnd(int damageWindowEnd) {
    this.damageWindowEnd = damageWindowEnd;
  }
  public int getDuration() {
    return duration;
  }
  public void setDuration(int duration) {
    this.duration = duration;
  }
  public int getEnergyDelta() {
    return energyDelta;
  }
  public void setEnergyDelta(int energyDelta) {
    this.energyDelta = energyDelta;
  }
  public int getMoveId() {
    return moveId;
  }
  public void setMoveId(int moveId) {
    this.moveId = moveId;
  }
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public int getDamage() {
    return damage;
  }
  public void setDamage(int damage) {
    this.damage = damage;
  }
  
  public String toString(){
    StringBuffer output = new StringBuffer();
    
    output.append(moveId + ": " + name);
    
    if(energyDelta < 0){
      int bars = 100/-(energyDelta);
      output.append(". " + bars + " bar");
      if(bars > 1){
        output.append("s");
      }
      output.append(".");
    }else{
      output.append(". Fast.");
    }
    
    output.append(" Damage: " + damage);
    
    return output.toString();
  }
}
