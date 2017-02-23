package pogobattles.gamemaster;

public class BasePokemon {
  
  private String name;
  private int pokemonId;
  private int candyToEvolve;
  private int[] chargeMoves;
  private int[] evolutionIds;
  private int familyId;
  private double kmBuddyDistance;
  private int parentPokemonId;
  private int[] quickMoves;
  private int baseAttack;
  private int baseDefense;
  private int baseStamina;
  private int type;
  private int type2;
  private double[] incomingDamageMultipliers;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getPokemonId() {
    return pokemonId;
  }
  public void setPokemonId(int pokemonId) {
    this.pokemonId = pokemonId;
  }
  public int getCandyToEvolve() {
    return candyToEvolve;
  }
  public void setCandyToEvolve(int candyToEvolve) {
    this.candyToEvolve = candyToEvolve;
  }
  public int[] getChargeMoves() {
    return chargeMoves;
  }
  public void setChargeMoves(int[] chargeMoves) {
    this.chargeMoves = chargeMoves;
  }
  public int[] getEvolutionIds() {
    return evolutionIds;
  }
  public void setEvolutionIds(int[] evolutionIds) {
    this.evolutionIds = evolutionIds;
  }
  public int getFamilyId() {
    return familyId;
  }
  public void setFamilyId(int familyId) {
    this.familyId = familyId;
  }
  public double getKmBuddyDistance() {
    return kmBuddyDistance;
  }
  public void setKmBuddyDistance(double kmBuddyDistance) {
    this.kmBuddyDistance = kmBuddyDistance;
  }
  public int getParentPokemonId() {
    return parentPokemonId;
  }
  public void setParentPokemonId(int parentPokemonId) {
    this.parentPokemonId = parentPokemonId;
  }
  public int[] getQuickMoves() {
    return quickMoves;
  }
  public void setQuickMoves(int[] quickMoves) {
    this.quickMoves = quickMoves;
  }
  public int getBaseAttack() {
    return baseAttack;
  }
  public void setBaseAttack(int baseAttack) {
    this.baseAttack = baseAttack;
  }
  public int getBaseDefense() {
    return baseDefense;
  }
  public void setBaseDefense(int baseDefense) {
    this.baseDefense = baseDefense;
  }
  public int getBaseStamina() {
    return baseStamina;
  }
  public void setBaseStamina(int baseStamina) {
    this.baseStamina = baseStamina;
  }
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public int getType2() {
    return type2;
  }
  public void setType2(int type2) {
    this.type2 = type2;
  }
  public double[] getIncomingDamageMultipliers() {
    return incomingDamageMultipliers;
  }
  public void setIncomingDamageMultipliers(double[] incomingDamageMultipliers) {
    this.incomingDamageMultipliers = incomingDamageMultipliers;
  }

  public String toString(){
    StringBuffer output = new StringBuffer();
    output.append(pokemonId + ": " + name + " t[" + type);
    if(type2 > 0){
      output.append(", " + type2);
    }
    output.append("]. (" + baseStamina + "/" + baseAttack + "/" + baseDefense + ") q[");
    for(int i = 0; i < quickMoves.length; i++){
      output.append(quickMoves[i]);
      if(i < quickMoves.length-1){
        output.append(", ");
      }
    }
    output.append("] c[");
    for(int i = 0; i < chargeMoves.length; i++){
      output.append(chargeMoves[i]);
      if(i < chargeMoves.length-1){
        output.append(", ");
      }
    }
    
    output.append("]");
    
    return output.toString();
  }

}
