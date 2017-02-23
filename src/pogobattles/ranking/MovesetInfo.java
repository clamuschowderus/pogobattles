package pogobattles.ranking;

public class MovesetInfo {
  private int pokemonId;
  private int quickMoveId;
  private int chargeMoveId;
  public MovesetInfo(int pokemonId, int quickMoveId, int chargeMoveId){
    this.pokemonId = pokemonId;
    this.quickMoveId = quickMoveId;
    this.chargeMoveId = chargeMoveId;
  }
  public int getPokemonId() {
    return pokemonId;
  }
  public void setPokemonId(int pokemonId) {
    this.pokemonId = pokemonId;
  }
  public int getQuickMoveId() {
    return quickMoveId;
  }
  public void setQuickMoveId(int quickMoveId) {
    this.quickMoveId = quickMoveId;
  }
  public int getChargeMoveId() {
    return chargeMoveId;
  }
  public void setChargeMoveId(int chargeMoveId) {
    this.chargeMoveId = chargeMoveId;
  }
  public Integer getUniqueId(){
    return (pokemonId * 1000000) + (quickMoveId * 1000) + chargeMoveId;
  }
}
