package pogobattles.gamemaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ParserNew {

  public static GameMaster parse(String gameMasterLocation) throws Exception{
    
    Hashtable<String, Integer> typeByName = new Hashtable<String, Integer>();
    typeByName.put("BUG", 7);
    typeByName.put("DARK", 17);
    typeByName.put("DRAGON", 16);
    typeByName.put("ELECTRIC", 13);
    typeByName.put("FAIRY", 18);
    typeByName.put("FIGHTING", 2);
    typeByName.put("FIRE", 10);
    typeByName.put("FLYING", 3);
    typeByName.put("GHOST", 8);
    typeByName.put("GRASS", 12);
    typeByName.put("GROUND", 5);
    typeByName.put("ICE", 15);
    typeByName.put("NORMAL", 1);
    typeByName.put("POISON", 4);
    typeByName.put("PSYCHIC", 14);
    typeByName.put("ROCK", 6);
    typeByName.put("STEEL", 9);
    typeByName.put("WATER", 11);
    
    FileReader gameMasterReader = new FileReader(gameMasterLocation);
    
    BufferedReader buffRead = new BufferedReader(gameMasterReader);
    
    //Temporary reference tables
    Hashtable<Integer, List<String>> pokemonQuickMovesTable = new Hashtable<Integer, List<String>>();
    Hashtable<Integer, List<String>> pokemonChargeMovesTable = new Hashtable<Integer, List<String>>();
    Hashtable<Integer, List<String>> pokemonEvolutionIdsTable = new Hashtable<Integer, List<String>>();
    Hashtable<Integer, String> pokemonParentNames = new Hashtable<Integer, String>();
    Hashtable<Integer, String> pokemonFamilyNames = new Hashtable<Integer, String>();
    Hashtable<String, Integer> pokemonIdByName = new Hashtable<String, Integer>();
    Hashtable<String, Integer> moveIdByName = new Hashtable<String, Integer>();
    
    //Final tables
    Hashtable<Integer, BasePokemon> pokemonTable = new Hashtable<Integer, BasePokemon>();
    Hashtable<Integer, Move> moveTable = new Hashtable<Integer, Move>();
    Hashtable<Integer, Type> typeTable = new Hashtable<Integer, Type>();
    Hashtable<Integer, Double> cpMultiplierTable = new Hashtable<Integer, Double>();
    double typeAdvantage[][] = new double[18][18];
    BattleSettings battleSettings = new BattleSettings();
    
    String line = null;
    
    String currentTemplateId = null;
    
    boolean inPokemon = false;
    boolean inMove = false;
    boolean inType = false;
    boolean inBattleSettings = false;
    BasePokemon currentPokemon = null;
    Move currentMove = null;
    Type currentType = null;
    double[] currentTypeAdvantage = null;

    while((line = buffRead.readLine())!=null){
      //System.out.println(line);
      if(line.contains("template_id:")){
        currentTemplateId = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
        inPokemon = false;
        inMove = false;
        inType = false;
        inBattleSettings = false;
      }
      if(line.contains("cp_multiplier")){
        double[] cpMultiplierArray = getDoubleArray("cp_multiplier", line, buffRead);
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
        String pokemonNumber = currentTemplateId.substring(currentTemplateId.indexOf("V")+1, currentTemplateId.indexOf("_POKEMON"));
        currentPokemon.setPokemonId(Integer.parseInt(pokemonNumber));
        pokemonTable.put(currentPokemon.getPokemonId(), currentPokemon);
      }
      
      if(line.contains("move_settings")){
        inMove = true;
        currentMove = new Move();
        String moveName = currentTemplateId.substring(currentTemplateId.lastIndexOf("MOVE_")+5, currentTemplateId.length()).replaceAll("_FAST", "");
        String moveNumber = currentTemplateId.substring(currentTemplateId.indexOf("V")+1, currentTemplateId.indexOf("_MOVE"));
        currentMove.setMoveId(Integer.parseInt(moveNumber));
        currentMove.setName(moveName.replaceAll(" FAST", ""));
        moveTable.put(currentMove.getMoveId(), currentMove);
        moveIdByName.put(currentMove.getName(), currentMove.getMoveId());
      }
      
      if(line.contains("type_effective")){
        inType = true;
        currentType = new Type();
        currentType.setName(currentTemplateId.substring(currentTemplateId.lastIndexOf("_")+1, currentTemplateId.length()));
        currentType.setTypeId(typeByName.get(currentType.getName()));
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
        if(line.contains("attack_scalar")){
          //System.out.println(currentType.getName());
          currentTypeAdvantage = getDoubleArray("attack_scalar", line, buffRead);
          typeAdvantage[typeByName.get(currentType.getName())-1] = currentTypeAdvantage;
          typeTable.put(currentType.getTypeId(), currentType);
        }
      }

      if(inMove){
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
          String typeName = getValue(line);
          typeName = typeName.substring(typeName.lastIndexOf("_")+1, typeName.length());
          currentMove.setType(typeByName.get(typeName));
        }
        if(line.contains("power:")){
          currentMove.setDamage((int)Double.parseDouble(getValue(line)));
        }
      }
      
      if(inPokemon){
        if(line.contains("cinematic_moves")){
          List<String> pokemonChargeMoves = pokemonChargeMovesTable.get(currentPokemon.getPokemonId());
          if(pokemonChargeMoves == null){
            pokemonChargeMoves = new ArrayList<String>();
            pokemonChargeMovesTable.put(currentPokemon.getPokemonId(), pokemonChargeMoves);
          }
          pokemonChargeMoves.add(getValue(line));
        }
        if(line.contains("quick_moves")){
          List<String> pokemonQuickMoves = pokemonQuickMovesTable.get(currentPokemon.getPokemonId());
          if(pokemonQuickMoves == null){
            pokemonQuickMoves = new ArrayList<String>();
            pokemonQuickMovesTable.put(currentPokemon.getPokemonId(), pokemonQuickMoves);
          }
          String moveName = getValue(line);
          // Hack for HIDDEN_POWER bug in GAME MASTER output
          if(moveName.equals("281")){
            moveName = "HIDDEN_POWER_FAST";
          }
          moveName = moveName.substring(0, moveName.lastIndexOf("_FAST"));
          pokemonQuickMoves.add(moveName);
        }
        if(line.contains("evolution_ids")){
          List<String> pokemonEvolutionIds = pokemonEvolutionIdsTable.get(currentPokemon.getPokemonId());
          if(pokemonEvolutionIds == null){
            pokemonEvolutionIds = new ArrayList<String>();
            pokemonEvolutionIdsTable.put(currentPokemon.getPokemonId(), pokemonEvolutionIds);
          }
          pokemonEvolutionIds.add(getValue(line));
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
          String familyName = getValue(line);
          familyName = familyName.substring(familyName.indexOf("_")+1, familyName.length());
          pokemonFamilyNames.put(currentPokemon.getPokemonId(), familyName);
        }
        if(line.contains("km_buddy_distance")){
          currentPokemon.setKmBuddyDistance(Double.parseDouble(getValue(line)));
        }
        if(line.contains("parent_pokemon_id")){
          //System.out.println(currentPokemon.getPokemonId());
          pokemonParentNames.put(currentPokemon.getPokemonId(), getValue(line));
        }
        if(line.contains("type: POKEMON_TYPE_") && !line.contains("type_2")){
          String typeName = getValue(line);
          typeName = typeName.substring(typeName.lastIndexOf("_")+1, typeName.length());
          currentPokemon.setType(typeByName.get(typeName));
        }
        if(line.contains("type_2")){
          String typeName = getValue(line);
          typeName = typeName.substring(typeName.lastIndexOf("_")+1, typeName.length());
          currentPokemon.setType2(typeByName.get(typeName));
        }
        if(line.contains("pokemon_id:") && !line.contains("parent_pokemon_id")){
          String pokemonName = getValue(line);
          //System.out.println(pokemonName);
          currentPokemon.setName(pokemonName);
          pokemonIdByName.put(pokemonName, currentPokemon.getPokemonId());
        }
      }
    }
    
    buffRead.close();
    gameMasterReader.close();
    
    Set<Integer> pokemonIds = pokemonTable.keySet();
    for(Integer pokemonId : pokemonIds){
      BasePokemon basePokemon = pokemonTable.get(pokemonId);
      //Hashtable<Integer, List<String>> pokemonQuickMovesTable = new Hashtable<Integer, List<String>>();
      //Hashtable<Integer, List<String>> pokemonChargeMovesTable = new Hashtable<Integer, List<String>>();
      //Hashtable<Integer, List<String>> pokemonEvolutionIdsTable = new Hashtable<Integer, List<String>>();
      //Hashtable<Integer, String> pokemonParentNames = new Hashtable<Integer, String>();
      //Hashtable<Integer, String> pokemonFamilyNames = new Hashtable<Integer, String>();
      //Hashtable<String, Integer> pokemonIdByName = new Hashtable<String, Integer>();
      //Hashtable<String, Integer> moveIdByName = new Hashtable<String, Integer>();
      String parentName = pokemonParentNames.get(pokemonId);
      if(parentName != null){
        //System.out.println(parentName);
        basePokemon.setParentPokemonId(pokemonIdByName.get(parentName));
      }
      //System.out.println(pokemonId);
      //System.out.println(pokemonFamilyNames.get(pokemonId));
      basePokemon.setFamilyId(pokemonIdByName.get(pokemonFamilyNames.get(pokemonId)));

      List<String> pokemonQuickMoves = pokemonQuickMovesTable.get(pokemonId);
      int[] quickMoves = new int[pokemonQuickMoves.size()];
      for(int i = 0; i < quickMoves.length; i++){
        //System.out.println(pokemonQuickMoves.get(i));
        quickMoves[i] = moveIdByName.get(pokemonQuickMoves.get(i));
      }

      List<String> pokemonChargeMoves = pokemonChargeMovesTable.get(pokemonId);
      int[] chargeMoves = new int[pokemonChargeMoves.size()];
      for(int i = 0; i < chargeMoves.length; i++){
        chargeMoves[i] = moveIdByName.get(pokemonChargeMoves.get(i));
      }

      List<String> pokemonEvolutionIds = pokemonEvolutionIdsTable.get(pokemonId);
      if(pokemonEvolutionIds != null){
        int[] evolutionIds = new int[pokemonEvolutionIds.size()];
        for(int i = 0; i < evolutionIds.length; i++){
          evolutionIds[i] = pokemonIdByName.get(pokemonEvolutionIds.get(i));
        }
        basePokemon.setEvolutionIds(evolutionIds);
      }
      
      basePokemon.setQuickMoves(quickMoves);
      basePokemon.setChargeMoves(chargeMoves);
    }
    
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
    String value = line.substring(line.lastIndexOf(":")+1, line.length()).replaceAll("}", "");
    return value.trim();
  }
  
  private static int[] getIntArray(String key, String line, BufferedReader buffRead) throws Exception {
    ArrayList<Integer> values = new ArrayList<Integer>();
    while(line.contains(key)){
      values.add(Integer.parseInt(line.substring(line.lastIndexOf(" ")+1, line.length()).trim()));
      line = buffRead.readLine();
    }
    int[] valuesInt = new int[values.size()];
    for(int i = 0; i < values.size(); i++){
      valuesInt[i] = values.get(i);
    }
    return valuesInt;
  }

  private static String[] getStringArray(String key, String line, BufferedReader buffRead) throws Exception {
    ArrayList<String> values = new ArrayList<String>();
    while(line.contains(key)){
      values.add(line.substring(line.lastIndexOf(" ")+1, line.length()).trim());
      line = buffRead.readLine();
    }
    String[] valuesString = new String[values.size()];
    for(int i = 0; i < values.size(); i++){
      valuesString[i] = values.get(i);
    }
    return valuesString;
  }

  private static double[] getDoubleArray(String key, String line, BufferedReader buffRead) throws Exception {
    ArrayList<Double> values = new ArrayList<Double>();
    int valueStartIdx = 0;
    while(line.contains(key)){
      line = line.trim();
      valueStartIdx = line.lastIndexOf(' ');
      String value = line.substring(valueStartIdx+1, line.length()).trim();
      if(value.equals("0.800000011920929")){
        value = "0.8"; // removing unnecessary floating point double precision leftover;
      }
      values.add(Double.parseDouble(value));
      line = buffRead.readLine();
    }
    double[] valuesDouble = new double[values.size()];
    for(int i = 0; i < values.size(); i++){
      valuesDouble[i] = values.get(i);
    }
    return valuesDouble;
  }
}
