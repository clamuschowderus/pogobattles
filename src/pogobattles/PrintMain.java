package pogobattles;

import java.io.File;
import java.io.FileWriter;

import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Parser;
import pogobattles.gamemaster.ParserNew;
import pogobattles.gamemaster.PrintUtil;
import pogobattles.ranking.MovesetTable;

public class PrintMain {
  
  public static final String BASE_FOLDER = "C:\\Personal\\PoGo\\Gen2.2\\"; 
  
  public static final String GAME_MASTER_LOCATION = BASE_FOLDER + "GAME_MASTER_GEN2.2.txt";
  public static final String ALL_MOVESETS = BASE_FOLDER + "AllMovesetsForSim.csv";
  
  public static void main(String[] args) throws Exception{
    System.out.println(new File(ALL_MOVESETS).getAbsolutePath());
    MovesetTable movesets = new MovesetTable();
    movesets.addFromFile(new File(ALL_MOVESETS));
    //GameMaster gameMaster = Parser.parse(GAME_MASTER_LOCATION);
    GameMaster gameMaster = ParserNew.parse(GAME_MASTER_LOCATION);
    movesets.addAllMovesets(gameMaster);
    System.out.println(movesets.getMovesetInfoTable().size());
    PrintUtil.printGameMaster(gameMaster);
    
    FileWriter output = new FileWriter(BASE_FOLDER + "MOVESETDPS.csv");
    PrintUtil.printMovesetDPS(output, gameMaster, movesets);
    output.flush();
    output.close();

    FileWriter movesetCombinationsOut = new FileWriter(BASE_FOLDER + "AllMovesets.csv");
    FileWriter pokemonStatsOut = new FileWriter(BASE_FOLDER + "CurrentStats.csv");
    FileWriter basicMovesOut = new FileWriter(BASE_FOLDER + "CurrentBasicMoves.csv");
    FileWriter chargeMovesOut = new FileWriter(BASE_FOLDER + "CurrentChargeMoves.csv");
    PrintUtil.printTrainingSpreadsheetData(movesetCombinationsOut, pokemonStatsOut, basicMovesOut, chargeMovesOut, gameMaster, movesets);
    movesetCombinationsOut.flush();
    movesetCombinationsOut.close();
    pokemonStatsOut.flush();
    pokemonStatsOut.close();
    basicMovesOut.flush();
    basicMovesOut.close();
    chargeMovesOut.flush();
    chargeMovesOut.close();
    
    FileWriter outputMovesets = new FileWriter(BASE_FOLDER + "AllMovesetsForSim.csv");
    PrintUtil.printMovesetsCSV(outputMovesets, movesets);
    outputMovesets.flush();
    outputMovesets.close();
  }
  
}
