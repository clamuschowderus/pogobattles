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
import pogobattles.battle.simulator.FightSimulator;
import pogobattles.battle.simulator.MonteCarloFightResult;
import pogobattles.battle.simulator.MonteCarloFightSimulator;
import pogobattles.battle.simulator.SingleFightSimulator;
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
    
    FightSimulator simulator = new SingleFightSimulator(gameMaster);
    FightSimulator monteCarloSimulator = new MonteCarloFightSimulator(gameMaster, 100);
    
    List<String> defenders = new ArrayList<String>();
    /*
    defenders.add("Venusaur");
    defenders.add("Charizard");
    defenders.add("Balstoise");
    defenders.add("Beedrill");
    defenders.add("Butterfree");
    defenders.add("Pidgeot");
    defenders.add("Raticate");
    defenders.add("Fearow");
    defenders.add("Arbok");
    defenders.add("Raichu");
    defenders.add("Sandslash");
    defenders.add("Nidoqueen");
    defenders.add("Nidoking");
    defenders.add("Clefable");
    defenders.add("Ninetales");
    defenders.add("Wigglytuff");
    defenders.add("Crobat");
    defenders.add("Vileplume");
    defenders.add("Bellossom");
    defenders.add("Parasect");
    defenders.add("Venomoth");
    defenders.add("Dugtrio");
    defenders.add("Persian");
    defenders.add("Golduck");
    defenders.add("Primeape");
    defenders.add("Arcanine");
    defenders.add("Poliwrath");
    defenders.add("Politoed");
    defenders.add("Alakazam");
    defenders.add("Machamp");
    defenders.add("Victreebel");
    defenders.add("Tentacruel");
    defenders.add("Golem");
    defenders.add("Rapidash");
    defenders.add("Slowbro");
    defenders.add("Slowking");
    defenders.add("Magneton");
    defenders.add("Farfetchd");
    defenders.add("Dodrio");
    defenders.add("Dewgong");
    defenders.add("Muk");
    defenders.add("Cloyster");
    defenders.add("Gengar");
    defenders.add("Steelix");
    defenders.add("Hypno");
    defenders.add("Kingler");
    defenders.add("Electrode");
    defenders.add("Exeggutor");
    defenders.add("Marowak");
    defenders.add("Hitmonlee");
    defenders.add("Hitmonchan");
    defenders.add("Lickitung");
    defenders.add("Weezing");
    defenders.add("Rhydon");
    defenders.add("Blissey");
    defenders.add("Tangela");
    defenders.add("Kangaskhan");
    defenders.add("Kingdra");
    defenders.add("Seaking");
    defenders.add("Starmie");
    defenders.add("MrMime");
    defenders.add("Scizor");
    defenders.add("Jynx");
    defenders.add("Electabuzz");
    defenders.add("Magmar");
    defenders.add("Pinsir");
    defenders.add("Tauros");
    defenders.add("Gyarados");
    defenders.add("Lapras");
    */
    defenders.add("Vaporeon");
    /*
    defenders.add("Jolteon");
    defenders.add("Flareon");
    defenders.add("Espeon");
    defenders.add("Umbreon");
    defenders.add("Porygon2");
    defenders.add("Omastar");
    defenders.add("Kabutops");
    defenders.add("Aerodactyl");
    defenders.add("Snorlax");
    defenders.add("Dragonite");
    */
    //GenII
    /*
    defenders.add("Meganium");
    defenders.add("Typhlosion");
    defenders.add("Feraligatr");
    defenders.add("Furret");
    defenders.add("Noctowl");
    defenders.add("Ledian");
    defenders.add("Ariados");
    defenders.add("Lanturn");
    defenders.add("Togetic");
    defenders.add("Xatu");
    defenders.add("Ampharos");
    defenders.add("Azumarill");
    defenders.add("Sudowoodo");
    defenders.add("Qwilfish");
    defenders.add("Jumpluff");
    defenders.add("Aipom");
    defenders.add("Sunflora");
    defenders.add("Yanma");
    defenders.add("Quagsire");
    defenders.add("Murkrow");
    defenders.add("Misdreavous");
    defenders.add("Girafarig");
    defenders.add("Wobbuffet");
    defenders.add("Shuckle");
    defenders.add("Forretress");
    defenders.add("Dunsparce");
    defenders.add("Gligar");
    defenders.add("Granbull");
    defenders.add("Miltank");
    defenders.add("Heracross");
    defenders.add("Sneasel");
    defenders.add("Ursaring");
    defenders.add("Magcargo");
    defenders.add("Piloswine");
    defenders.add("Corsola");
    defenders.add("Octillery");
    defenders.add("Mantine");
    defenders.add("Skarmory");
    defenders.add("Houndoom");
    defenders.add("Donphan");
    defenders.add("Stantler");
    defenders.add("Hitmontop");
    defenders.add("Tyranitar");
    */
    
    List<String> attackers = new ArrayList<String>();
    attackers.add("Venusaur");
    attackers.add("Charizard");
    attackers.add("Balstoise");
    attackers.add("Beedrill");
    attackers.add("Butterfree");
    attackers.add("Pidgeot");
    attackers.add("Raticate");
    attackers.add("Fearow");
    attackers.add("Arbok");
    attackers.add("Raichu");
    attackers.add("Sandslash");
    attackers.add("Nidoqueen");
    attackers.add("Nidoking");
    attackers.add("Clefable");
    attackers.add("Ninetales");
    attackers.add("Wigglytuff");
    attackers.add("Crobat");
    attackers.add("Vileplume");
    attackers.add("Bellossom");
    attackers.add("Parasect");
    attackers.add("Venomoth");
    attackers.add("Dugtrio");
    attackers.add("Persian");
    attackers.add("Golduck");
    attackers.add("Primeape");
    attackers.add("Arcanine");
    attackers.add("Poliwrath");
    attackers.add("Politoed");
    attackers.add("Alakazam");
    attackers.add("Machamp");
    attackers.add("Victreebel");
    attackers.add("Tentacruel");
    attackers.add("Golem");
    attackers.add("Rapidash");
    attackers.add("Slowbro");
    attackers.add("Slowking");
    attackers.add("Magneton");
    attackers.add("Farfetchd");
    attackers.add("Dodrio");
    attackers.add("Dewgong");
    attackers.add("Muk");
    attackers.add("Cloyster");
    attackers.add("Gengar");
    attackers.add("Steelix");
    attackers.add("Hypno");
    attackers.add("Kingler");
    attackers.add("Electrode");
    attackers.add("Exeggutor");
    attackers.add("Marowak");
    attackers.add("Hitmonlee");
    attackers.add("Hitmonchan");
    attackers.add("Lickitung");
    attackers.add("Weezing");
    attackers.add("Rhydon");
    attackers.add("Blissey");
    attackers.add("Tangela");
    attackers.add("Kangaskhan");
    attackers.add("Kingdra");
    attackers.add("Seaking");
    attackers.add("Starmie");
    attackers.add("MrMime");
    attackers.add("Scizor");
    attackers.add("Jynx");
    attackers.add("Electabuzz");
    attackers.add("Magmar");
    attackers.add("Pinsir");
    attackers.add("Tauros");
    attackers.add("Gyarados");
    attackers.add("Lapras");
    attackers.add("Vaporeon");
    attackers.add("Jolteon");
    attackers.add("Flareon");
    attackers.add("Espeon");
    attackers.add("Umbreon");
    attackers.add("Porygon2");
    attackers.add("Omastar");
    attackers.add("Kabutops");
    attackers.add("Aerodactyl");
    attackers.add("Snorlax");
    attackers.add("Dragonite");
    //GenII
    attackers.add("Meganium");
    attackers.add("Typhlosion");
    attackers.add("Feraligatr");
    attackers.add("Furret");
    attackers.add("Noctowl");
    attackers.add("Ledian");
    attackers.add("Ariados");
    attackers.add("Lanturn");
    attackers.add("Togetic");
    attackers.add("Xatu");
    attackers.add("Ampharos");
    attackers.add("Azumarill");
    attackers.add("Sudowoodo");
    attackers.add("Qwilfish");
    attackers.add("Jumpluff");
    attackers.add("Aipom");
    attackers.add("Sunflora");
    attackers.add("Yanma");
    attackers.add("Quagsire");
    attackers.add("Murkrow");
    attackers.add("Misdreavous");
    attackers.add("Girafarig");
    attackers.add("Wobbuffet");
    attackers.add("Shuckle");
    attackers.add("Forretress");
    attackers.add("Dunsparce");
    attackers.add("Gligar");
    attackers.add("Granbull");
    attackers.add("Miltank");
    attackers.add("Heracross");
    attackers.add("Sneasel");
    attackers.add("Ursaring");
    attackers.add("Magcargo");
    attackers.add("Piloswine");
    attackers.add("Corsola");
    attackers.add("Octillery");
    attackers.add("Mantine");
    attackers.add("Skarmory");
    attackers.add("Houndoom");
    attackers.add("Donphan");
    attackers.add("Stantler");
    attackers.add("Hitmontop");
    attackers.add("Tyranitar");

    String outputFolder = BASE_FOLDER + "CountersMaxHuman\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 300, 400);
    //RankingCalculator.calculateAll(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 300, 400);
    
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
    
    //outputFolder = BASE_FOLDER + "ByPrestigerDefLevel30\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, /*attackers*/null, defenders, 300, false);

    //outputFolder = BASE_FOLDER + "ByPrestigerDefLevel20Meta\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, /*attackers*/null, defenders, 200, false);

    //outputFolder = BASE_FOLDER + "MonteCarlo-1000Prestige20-39\\";
    //checkAndCreateFolder(outputFolder);
    //RankingCalculator.calculateAllPrestigeLevelsRange(outputFolder, monteCarloSimulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, 200, 390, false);

    outputFolder = BASE_FOLDER + "MonteCarlo-1000Prestige20-39\\";
    checkAndCreateFolder(outputFolder);
    RankingCalculator.calculateAllPrestigeLevelsRange(outputFolder, monteCarloSimulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, 200, 390, false);

    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    BasePokemon attBase = pokemonByName(gameMaster, "ALAKAZAM");
    Move attQm = moveByName(gameMaster, "PSYCHO_CUT");
    Move attCm = moveByName(gameMaster, "FUTURESIGHT");
    BasePokemon defBase = pokemonByName(gameMaster, "RHYDON");
    Move defQm = moveByName(gameMaster, "MUD_SLAP");
    Move defCm = moveByName(gameMaster, "STONE_EDGE");
    Pokemon attacker = creator.createPokemon(attBase, 300, 15, 15, 15, attQm, attCm);
    Pokemon defender = creator.createPokemon(defBase, 400, 15, 15, 15, defQm, defCm);
    MonteCarloFightResult result = (MonteCarloFightResult)monteCarloSimulator.calculateAttackDPS(attacker, defender, AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN, AttackStrategyType.DEFENSE_RANDOM);
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
