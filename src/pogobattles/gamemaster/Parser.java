package pogobattles.gamemaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class Parser {

  public static GameMaster parse(String gameMasterLocation) throws Exception{
    FileReader gameMasterReader = new FileReader(gameMasterLocation);
    
    BufferedReader buffRead = new BufferedReader(gameMasterReader);
    
    Hashtable<Integer, BasePokemon> pokemonTable = new Hashtable<Integer, BasePokemon>();
    Hashtable<Integer, Move> moveTable = new Hashtable<Integer, Move>();
    Hashtable<Integer, Type> typeTable = new Hashtable<Integer, Type>();
    Hashtable<Integer, Double> cpMultiplierTable = new Hashtable<Integer, Double>();
    double typeAdvantage[][] = new double[18][18];
    BattleSettings battleSettings = new BattleSettings();
    
    String line = null;
    
    boolean inPokemon = false;
    boolean inMove = false;
    boolean inType = false;
    boolean inBattleSettings = false;
    BasePokemon currentPokemon = null;
    Move currentMove = null;
    Type currentType = null;
    double[] currentTypeAdvantage = null;

    while((line = buffRead.readLine())!=null){
      if(line.contains("cp_multiplier")){
        double[] cpMultiplierArray = getDoubleArray(line, buffRead);
        double lastCpM = 0.0;
        for(int i = 0; i < cpMultiplierArray.length; i++){
          int realLevel = (i * 10)+5;
          if(lastCpM != 0.0){
            cpMultiplierTable.put(realLevel, (lastCpM+cpMultiplierArray[i])/2);
          }
          realLevel += 5;
          cpMultiplierTable.put(realLevel, cpMultiplierArray[i]);
          lastCpM = cpMultiplierArray[i];
        }
      }
      
      if(line.contains("pokemon_settings")){
        inPokemon = true;
        currentPokemon = new BasePokemon();
      }
      
      if(line.contains("move_settings")){
        inMove = true;
        currentMove = new Move();
      }
      
      if(line.contains("POKEMON_TYPE_")){
        inType = true;
        currentType = new Type();
        currentType.setName(line.substring(line.lastIndexOf("_")+1, line.length()-2));
      }
      
      if(line.contains("battle_settings")){
        inBattleSettings = true;
      }

      if(inBattleSettings){
        if(line.contains("dodge_damage_reduction_percent")){
          battleSettings.setDodgeDamageReduction(Double.parseDouble(getValue(line)));
        }
        if(line.contains("dodge_duration_ms")){
          battleSettings.setDodgeDuration(Integer.parseInt(getValue(line)));
          Move.DODGE_MOVE.setDuration(battleSettings.getDodgeDuration()); // Setting dodge duration from Game Master
        }
        if(line.contains("enemy_attack_interval")){
          battleSettings.setEnemyAttackInterval(Double.parseDouble(getValue(line)));
        }
        if(line.contains("energy_delta_per_health_lost")){
          battleSettings.setEnergyDeltaPerHealthLost(Double.parseDouble(getValue(line)));
        }
        if(line.contains("maximum_energy")){
          battleSettings.setMaximumEnergy(Integer.parseInt(getValue(line)));
        }
        if(line.contains("round_duration_seconds")){
          battleSettings.setRoundDuration(Double.parseDouble(getValue(line)));
        }
        if(line.contains("same_type_attack_bonus_multiplier")){
          battleSettings.setStabMultiplier(Double.parseDouble(getValue(line)));
        }
        if(line.contains("BATTLE_SETTINGS")){
          inBattleSettings = false;
        }
      }
      
      if(inType){
        if(line.contains("type_effective")){
          currentTypeAdvantage = getDoubleArray(line, buffRead);
        }
        if(line.contains("attack_type")){
          currentType.setTypeId(Integer.parseInt(getValue(line)));
          inType = false;
          typeAdvantage[currentType.getTypeId()-1] = currentTypeAdvantage;
          typeTable.put(currentType.getTypeId(), currentType);
        }
      }

      if(inMove){
        if(line.contains("template_id")){
          String moveName = line.substring(line.lastIndexOf("MOVE_")+5, line.length()-3).replaceAll("_FAST", "");
          String moveNumber = line.substring(line.indexOf("u'V")+3, line.indexOf("_MOVE"));
          currentMove.setMoveId(Integer.parseInt(moveNumber));
          currentMove.setName(moveName.replaceAll(" FAST", ""));
          moveTable.put(currentMove.getMoveId(), currentMove);
          inMove = false;
        }
        if(line.contains("accuracy_chance")){
          currentMove.setAccuracyChance(Double.parseDouble(getValue(line)));
        }
        if(line.contains("critical_chance")){
          currentMove.setCriticalChance(Double.parseDouble(getValue(line)));
        }
        if(line.contains("damage_window_end_ms")){
          currentMove.setDamageWindowEnd(Integer.parseInt(getValue(line)));
        }
        if(line.contains("damage_window_start_ms")){
          currentMove.setDamageWindowStart(Integer.parseInt(getValue(line)));
        }
        if(line.contains("duration_ms")){
          currentMove.setDuration(Integer.parseInt(getValue(line)));
        }
        if(line.contains("energy_delta")){
          currentMove.setEnergyDelta(Integer.parseInt(getValue(line)));
        }
        if(line.contains("pokemon_type")){
          currentMove.setType(Integer.parseInt(getValue(line)));
        }
        if(line.contains("movement_id")){
          currentMove.setMoveId(Integer.parseInt(getValue(line)));
        }
        if(line.contains("'power':")){
          currentMove.setDamage((int)Double.parseDouble(getValue(line)));
        }
      }
      
      if(inPokemon){
        if(line.contains("template_id")){
          String pokemonName = line.substring(line.lastIndexOf("POKEMON_")+8, line.length()-3);
          String pokemonNumber = line.substring(line.indexOf("u'V")+3, line.indexOf("_POKEMON"));
          currentPokemon.setPokemonId(Integer.parseInt(pokemonNumber));
          currentPokemon.setName(pokemonName);
          pokemonTable.put(currentPokemon.getPokemonId(), currentPokemon);
          inPokemon = false;
        }
        if(line.contains("cinematic_moves")){
          currentPokemon.setChargeMoves(getIntArray(line, buffRead));
        }
        if(line.contains("quick_moves")){
          currentPokemon.setQuickMoves(getIntArray(line, buffRead));
        }
        if(line.contains("evolution_ids")){
          currentPokemon.setEvolutionIds(getIntArray(line, buffRead));
        }
        if(line.contains("base_attack")){
          currentPokemon.setBaseAttack(Integer.parseInt(getValue(line)));
        }
        if(line.contains("base_defense")){
          currentPokemon.setBaseDefense(Integer.parseInt(getValue(line)));
        }
        if(line.contains("base_stamina")){
          currentPokemon.setBaseStamina(Integer.parseInt(getValue(line)));
        }
        if(line.contains("candy_to_evolve")){
          currentPokemon.setCandyToEvolve(Integer.parseInt(getValue(line)));
        }
        if(line.contains("family_id")){
          currentPokemon.setFamilyId(Integer.parseInt(getValue(line)));
        }
        if(line.contains("km_buddy_distance")){
          currentPokemon.setKmBuddyDistance(Double.parseDouble(getValue(line)));
        }
        if(line.contains("parent_pokemon_id")){
          currentPokemon.setParentPokemonId(Integer.parseInt(getValue(line)));
        }
        if(line.contains("type") && !line.contains("type_2")){
          currentPokemon.setType(Integer.parseInt(getValue(line)));
        }
        if(line.contains("type_2")){
          currentPokemon.setType2(Integer.parseInt(getValue(line)));
        }
      }
    }
    
    buffRead.close();
    gameMasterReader.close();
    
    GameMaster gameMaster = new GameMaster();
    gameMaster.setPokemonTable(pokemonTable);
    gameMaster.setMoveTable(moveTable);
    gameMaster.setTypeAdvantage(typeAdvantage);
    gameMaster.setBattleSettings(battleSettings);
    gameMaster.setTypeTable(typeTable);
    gameMaster.setCpMultiplierTable(cpMultiplierTable);
    
    calculateIncomingDamageMultipliers(gameMaster);

    return gameMaster;
  }
  
  private static void calculateIncomingDamageMultipliers(GameMaster gameMaster){
    Hashtable<Integer, BasePokemon> pokemonTable = gameMaster.getPokemonTable();
    double[][] typeAdvantage = gameMaster.getTypeAdvantage();
    Set<Integer> keySet = pokemonTable.keySet();
    for(Integer dexNum : keySet){
      BasePokemon pokemon = pokemonTable.get(dexNum);
      double[] type1Mult = new double[typeAdvantage[0].length];
      for(int i = 0; i < type1Mult.length; i++){
        type1Mult[i] = typeAdvantage[i][pokemon.getType()-1];
      }
      if(pokemon.getType2() == 0){
        pokemon.setIncomingDamageMultipliers(type1Mult);
      }else{
        double[] type2Mult = new double[type1Mult.length];
        for(int i = 0; i < type1Mult.length; i++){
          type2Mult[i] = typeAdvantage[i][pokemon.getType2()-1]*type1Mult[i];
        }
        pokemon.setIncomingDamageMultipliers(type2Mult);
      }
    }
  }
  
  private static String getValue(String line){
    String value = line.substring(line.lastIndexOf(":")+1, line.length()-1).replaceAll("}", "");
    return value.trim();
  }
  
  private static int[] getIntArray(String line, BufferedReader buffRead) throws Exception {
    ArrayList<Integer> values = new ArrayList<Integer>();
    while(!line.contains("]")){
      values.add(Integer.parseInt(line.substring(line.length()-5, line.length()-1).trim()));
      line = buffRead.readLine();
    }
    values.add(Integer.parseInt(line.substring(line.length()-6, line.length()-2).trim()));
    int[] valuesInt = new int[values.size()];
    for(int i = 0; i < values.size(); i++){
      valuesInt[i] = values.get(i);
    }
    return valuesInt;
  }

  private static double[] getDoubleArray(String line, BufferedReader buffRead) throws Exception {
    ArrayList<Double> values = new ArrayList<Double>();
    int valueStartIdx = 0;
    while(!line.contains("]")){
      line = line.trim();
      valueStartIdx = line.lastIndexOf(' ');
      String value = line.substring(valueStartIdx+1, line.length()-1).trim();
      if(value.equals("0.800000011920929")){
        value = "0.8"; // removing unnecessary floating point double precision leftover;
      }
      values.add(Double.parseDouble(value));
      line = buffRead.readLine();
    }
    valueStartIdx = line.lastIndexOf(' ');
    String value = line.substring(valueStartIdx+1, line.length()-2).trim();
    if(value.equals("0.800000011920929")){
      value = "0.8"; // removing unnecessary floating point double precision leftover;
    }
    values.add(Double.parseDouble(value));
    double[] valuesDouble = new double[values.size()];
    for(int i = 0; i < values.size(); i++){
      valuesDouble[i] = values.get(i);
    }
    return valuesDouble;
  }
}
