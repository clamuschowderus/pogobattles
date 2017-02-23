package pogobattles.ranking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Set;

import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;

public class MovesetTable {
  private Hashtable<Integer, MovesetInfo> movesetTable = new Hashtable<Integer, MovesetInfo>();

  public void addAllMovesets(GameMaster gameMaster){
    Set<Integer> pokemonKeys = gameMaster.getPokemonTable().keySet();
    for(int pokemonId : pokemonKeys){
      BasePokemon basePokemon = gameMaster.getPokemonTable().get(pokemonId);
      for(int i = 0; i < basePokemon.getQuickMoves().length; i++){
        for(int j = 0; j < basePokemon.getChargeMoves().length; j++){
          int quickMoveId = basePokemon.getQuickMoves()[i];
          int chargeMoveId = basePokemon.getChargeMoves()[j];
          MovesetInfo info = new MovesetInfo(basePokemon.getPokemonId(), quickMoveId, chargeMoveId);
          movesetTable.put(info.getUniqueId(), info);
        }
      }
    }
  }
  
  public void addFromFile(File file) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = null;
    while((line = reader.readLine())!=null){
      String[] values = line.split(",");
      MovesetInfo info = new MovesetInfo(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
      movesetTable.put(info.getUniqueId(), info);
    }
    reader.close();
  }
  
  public Hashtable<Integer, MovesetInfo> getMovesetInfoTable(){
    return movesetTable;
  }
}
