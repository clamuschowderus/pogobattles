package pogobattles;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.simulator.AttackSimulator;
import pogobattles.battle.simulator.FightResult;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;
import pogobattles.gamemaster.ParserNew;
import pogobattles.gamemaster.PrintUtil;
import pogobattles.ranking.MovesetTable;
import pogobattles.ranking.RankingCalculator;

public class Main {
  public static final String BASE_FOLDER = "C:\\Personal\\PoGo\\Gen2.2\\";
  
  public static final String GAME_MASTER_LOCATION = BASE_FOLDER + "GAME_MASTER_GEN2.2.txt";
  public static final String MOVESETS_FOR_RANKINGS = BASE_FOLDER + "AllMovesetsForSim.csv";
  //public static final String DEFENDER_LIST = "DefenderNameList.csv"; //not yet used
  
  public static void main(String[] args) throws Exception {
    MovesetTable movesets = new MovesetTable();
    movesets.addFromFile(new File(MOVESETS_FOR_RANKINGS));
    GameMaster gameMaster = ParserNew.parse(GAME_MASTER_LOCATION);
    
    Set<Integer> cpMLevels = gameMaster.getCpMultiplierTable().keySet();
    SortedSet<Integer> sortedLevels = new TreeSet<Integer>();
    sortedLevels.addAll(cpMLevels);
    /*
    for(Integer level : sortedLevels){
      System.out.println(level + ": " + gameMaster.getCpMultiplierTable().get(level));
    }
    */
    
    AttackSimulator simulator = new AttackSimulator(gameMaster);
    
    List<String> defenders = new ArrayList<String>();
    //defenders.add("Blissey");
    defenders.add("Dragonite");
    //defenders.add("Snorlax");
    //defenders.add("Rhydon");
    //defenders.add("Tyranitar");
    //defenders.add("Gyarados");
    //defenders.add("Vaporeon");
    //defenders.add("Exeggutor");
    //defenders.add("Espeon");
    //defenders.add("Jolteon");
    //defenders.add("Donphan");
    //defenders.add("Flareon");
    //defenders.add("Machamp");
    //defenders.add("Scizor");
    //defenders.add("Muk");
    //defenders.add("Lapras");
    //defenders.add("Granbull");

    //defenders.add("Steelix");
    //defenders.add("Poliwrath");
    //defenders.add("Arcanine");
    //defenders.add("Charizard");
    
    List<String> attackers = new ArrayList<String>();
    attackers.add("Chansey");
    attackers.add("Blissey");
    attackers.add("Umbreon");
    attackers.add("Steelix");
    attackers.add("Forretress");
    attackers.add("Skarmory");
    attackers.add("Lapras");
    attackers.add("Azumarill");
    attackers.add("Wigglytuff");
    attackers.add("Sudowoodo");
    attackers.add("Snorlax");
    attackers.add("Lickitung");
    attackers.add("Lanturn");
    attackers.add("Noctowl");
    attackers.add("Dewgong");
    attackers.add("Magcargo");
    attackers.add("Dunsparce");
    attackers.add("Quagsire");
    attackers.add("Furret");
    attackers.add("Poliwrath");
    attackers.add("Tangela");
    attackers.add("Magneton");
    attackers.add("Haunter");
    attackers.add("Scizor");
    attackers.add("Parasect");
    attackers.add("Raticate");
    attackers.add("Wobuffet");
    

    String outputFolder = BASE_FOLDER + "CountersMax\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 300, 400);

    //outputFolder = BASE_FOLDER + "Counters30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 300, 300);

    //outputFolder = BASE_FOLDER + "Counters20\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 300, 200);
    
    //outputFolder = BASE_FOLDER + "PrestigersMax\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 400, true);

    //outputFolder = BASE_FOLDER + "Prestigers30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 300, true);

    //outputFolder = BASE_FOLDER + "Prestigers20\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 200, true);

    //outputFolder = BASE_FOLDER + "1000PrestigeMax\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 400, false);

    //outputFolder = BASE_FOLDER + "1000Prestige30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 300, false);

    //outputFolder = BASE_FOLDER + "1000Prestige20\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 200, false);

    //outputFolder = BASE_FOLDER + "Prestigers0AttMax\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 15, 15, 400, 15, 15, 15, 400, true);

    //outputFolder = BASE_FOLDER + "Prestigers0Att30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 15, 15, 400, 15, 15, 15, 300, true);

    //outputFolder = BASE_FOLDER + "Prestigers0Att20\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 15, 15, 400, 15, 15, 15, 200, true);

    //outputFolder = BASE_FOLDER + "Prestigers0Att0DefMax\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 0, 15, 400, 15, 15, 15, 400, true);

    //outputFolder = BASE_FOLDER + "Prestigers0Att0Def30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 0, 15, 400, 15, 15, 15, 300, true);

    //outputFolder = BASE_FOLDER + "Prestigers0Att0Def20\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 0, 0, 15, 400, 15, 15, 15, 200, true);
    
    //outputFolder = BASE_FOLDER + "ByPrestigerDefLevel30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, /*attackers*/null, /*defenders*/null, 300, false);
    
    //outputFolder = BASE_FOLDER + "ByPrestigerDefLevel20Meta\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, /*attackers*/null, defenders, 200, false);

    //outputFolder = BASE_FOLDER + "1000Prestige20-39\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeLevelsRange(outputFolder, simulator, gameMaster, movesets, defenders, 0, 0, 15, 400, 15, 15, 15, 200, 390, false);

    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    BasePokemon attBase = pokemonByName(gameMaster, "ABRA");
    Move attQm = moveByName(gameMaster, "CHARGE_BEAM");
    Move attCm = moveByName(gameMaster, "PSYSHOCK");
    BasePokemon defBase = pokemonByName(gameMaster, "ARIADOS");
    Move defQm = moveByName(gameMaster, "INFESTATION");
    Move defCm = moveByName(gameMaster, "CROSS_POISON");
    Pokemon attacker = creator.createPokemon(attBase, 210, 15, 15, 15, attQm, attCm);
    Pokemon defender = creator.createPokemon(defBase, 300, 15, 15, 15, defQm, defCm);
    FightResult result = simulator.calculateAttackDPS(attacker, defender, AttackStrategyType.DODGE_ALL, AttackStrategyType.DEFENSE);
    OutputStreamWriter writer = new OutputStreamWriter(System.out);
    System.out.println();
    PrintUtil.printFightResult(writer, result);
    writer.flush();

  }
  
  private static BasePokemon pokemonByName(GameMaster gameMaster, String name){
    Set<Integer> dexNumList = gameMaster.getPokemonTable().keySet();
    for(Integer dexNum : dexNumList){
      if(gameMaster.getPokemonTable().get(dexNum).getName().equals(name)){
        return gameMaster.getPokemonTable().get(dexNum);
      }
    }
    return null;
  }
  
  private static Move moveByName(GameMaster gameMaster, String name){
    Set<Integer> moveNumList = gameMaster.getMoveTable().keySet();
    for(Integer moveNum : moveNumList){
      if(gameMaster.getMoveTable().get(moveNum).getName().equals(name)){
        return gameMaster.getMoveTable().get(moveNum);
      }
    }
    return null;
  }
  
  private static void checkAndCreateFolder(String folderPath){
    File folder = new File(folderPath);
    if(!folder.exists()){
      folder.mkdirs();
    }
  }
}
