package pogobattles.gamemaster;

import java.util.Hashtable;

public class GameMaster {
  private Hashtable<Integer, BasePokemon> pokemonTable = new Hashtable<Integer, BasePokemon>();
  private Hashtable<Integer, Move> moveTable = new Hashtable<Integer, Move>();
  private Hashtable<Integer, Type> typeTable = new Hashtable<Integer, Type>();
  private Hashtable<Integer, Double> cpMultiplierTable = new Hashtable<Integer, Double>();
  private double typeAdvantage[][] = new double[18][18];
  private BattleSettings battleSettings = new BattleSettings();
  
  private final int MAX_INDIVIDUAL_STAT = 15; // Fixed value not found in game master;
  
  public Hashtable<Integer, BasePokemon> getPokemonTable() {
    return pokemonTable;
  }
  public void setPokemonTable(Hashtable<Integer, BasePokemon> pokemonTable) {
    this.pokemonTable = pokemonTable;
  }
  public Hashtable<Integer, Move> getMoveTable() {
    return moveTable;
  }
  public void setMoveTable(Hashtable<Integer, Move> moveTable) {
    this.moveTable = moveTable;
  }
  public Hashtable<Integer, Type> getTypeTable() {
    return typeTable;
  }
  public void setTypeTable(Hashtable<Integer, Type> typeTable) {
    this.typeTable = typeTable;
  }
  public double[][] getTypeAdvantage() {
    return typeAdvantage;
  }
  public void setTypeAdvantage(double[][] typeAdvantage) {
    this.typeAdvantage = typeAdvantage;
  }
  public BattleSettings getBattleSettings() {
    return battleSettings;
  }
  public void setBattleSettings(BattleSettings battleSettings) {
    this.battleSettings = battleSettings;
  }
  public Hashtable<Integer, Double> getCpMultiplierTable() {
    return cpMultiplierTable;
  }
  public void setCpMultiplierTable(Hashtable<Integer, Double> cpMultiplierTable) {
    this.cpMultiplierTable = cpMultiplierTable;
  }
  public int getMaxIvStat(){
    return MAX_INDIVIDUAL_STAT;
  }
}
