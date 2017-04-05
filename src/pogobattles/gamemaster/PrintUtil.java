package pogobattles.gamemaster;

import java.io.FileWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pogobattles.battle.simulator.CombatResult;
import pogobattles.battle.simulator.CombatantResult;
import pogobattles.battle.simulator.FightResult;
import pogobattles.battle.simulator.MonteCarloFightResult;
import pogobattles.ranking.MovesetInfo;
import pogobattles.ranking.MovesetTable;

public class PrintUtil {
  
  public static void printGameMaster(GameMaster gameMaster){
    Hashtable<Integer, BasePokemon> pokemonTable = gameMaster.getPokemonTable();
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    
    Set<Integer> pokemonIds = pokemonTable.keySet();
    Set<Integer> moveIds = moveTable.keySet();
    
    SortedSet<Integer> pokemonIdsSorted = new TreeSet<Integer>();
    pokemonIdsSorted.addAll(pokemonIds);
    
    SortedSet<Integer> moveIdsSorted = new TreeSet<Integer>();
    moveIdsSorted.addAll(moveIds);
    
    System.out.println("POKEMON:\r\n");

    for(Integer index : pokemonIdsSorted){
      System.out.println(printBasePokemon(gameMaster, pokemonTable.get(index)));
      System.out.println();
    }
    
    Move minChargeStart = null;
    Move maxChargeStart = null;
    Move minFastStart = null;
    Move maxFastStart = null;
    System.out.println("\r\n\r\nMOVES:\r\n");
    
    for(Integer index : moveIdsSorted){
      Move currentMove = moveTable.get(index);
      if(currentMove.getEnergyDelta() < 0){
        if(minChargeStart == null){
          minChargeStart = currentMove;
          maxChargeStart = currentMove;
        }else{
          if(minChargeStart.getDamageWindowStart() > currentMove.getDamageWindowStart()){
            minChargeStart = currentMove;
          }
          if(maxChargeStart.getDamageWindowStart() < currentMove.getDamageWindowStart()){
            maxChargeStart = currentMove;
          }
        }
      }else if(currentMove.getEnergyDelta() > 0){
        if(minFastStart == null){
          minFastStart = currentMove;
          maxFastStart = currentMove;
        }else{
          if(minFastStart.getDamageWindowStart() > currentMove.getDamageWindowStart()){
            minFastStart = currentMove;
          }
          if(maxFastStart.getDamageWindowStart() < currentMove.getDamageWindowStart()){
            maxFastStart = currentMove;
          }
        }
      }//ignore zero energy moves
      System.out.println(printMove(gameMaster, currentMove));
      System.out.println();
    }
    
    System.out.println("Fastest fast: " + minFastStart.getName() + ". " + minFastStart.getDamageWindowStart() + "ms");
    System.out.println("Slowest fast: " + maxFastStart.getName() + ". " + maxFastStart.getDamageWindowStart() + "ms");
    System.out.println("Fastest charge: " + minChargeStart.getName() + ". " + minChargeStart.getDamageWindowStart() + "ms");
    System.out.println("Slowest charge: " + maxChargeStart.getName() + ". " + maxChargeStart.getDamageWindowStart() + "ms");
  }
  
  public static String printBasePokemon(GameMaster gameMaster, BasePokemon pokemon){
    StringBuffer output = new StringBuffer();
    
    Hashtable<Integer, Type> typeTable = gameMaster.getTypeTable();
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    
    System.out.println(pokemon.getName());
    System.out.println("TypeId: " + pokemon.getType());
    System.out.println("Type: " + typeTable==null?"tablenull":typeTable.get(pokemon.getType()).getName());
    
    output.append(pokemon.getPokemonId() + ": " + pokemon.getName() + " t[" + typeTable.get(pokemon.getType()).getName());
    if(pokemon.getType2() > 0){
      output.append("/" + typeTable.get(pokemon.getType2()).getName());
    }
    output.append("]. (" + pokemon.getBaseStamina() + "/" + pokemon.getBaseAttack() + "/" + pokemon.getBaseDefense() + ") q[");
    
    int[] quickMoves = pokemon.getQuickMoves();
    
    for(int i = 0; i < quickMoves.length; i++){
      output.append(moveTable.get(quickMoves[i]).getName());
      if(i < quickMoves.length-1){
        output.append(", ");
      }
    }
    output.append("] c[");
    
    int[] chargeMoves = pokemon.getChargeMoves();
    
    for(int i = 0; i < chargeMoves.length; i++){
      output.append(moveTable.get(chargeMoves[i]).getName());
      if(i < chargeMoves.length-1){
        output.append(", ");
      }
    }
    
    /*
    
    output.append("] dmg[");
    
    double[] idm = pokemon.getIncomingDamageMultipliers();
    boolean firstIdmOutput = true;
    
    for(int i = 0; i < idm.length; i++){
      if(idm[i] != 1){
        if(firstIdmOutput){
          firstIdmOutput = false;
        }else{
          output.append(", ");
        }
        String idmString = idm[i] + "";
        if(idmString.startsWith("0.64")){
          idmString = "0.64";
        }
        output.append(typeTable.get(i+1).getName() + " x" + idmString);
      }
    }
    
    */
    
    output.append("]");
    
    return output.toString();
  }
  
  public static String printMove(GameMaster gameMaster, Move move){
    StringBuffer output = new StringBuffer();
    
    Hashtable<Integer, Type> typeTable = gameMaster.getTypeTable();
    
    output.append(move.getMoveId() + ": " + move.getName() + "(" + typeTable.get(move.getType()).getName() + ")");
    
    if(move.getEnergyDelta() < 0){
      int bars = 100/-(move.getEnergyDelta());
      output.append(". " + bars + " bar");
      if(bars > 1){
        output.append("s");
      }
      output.append(".");
    }else{
      output.append(". Fast.");
    }
    
    output.append(" Dmg: " + move.getDamage());
    
    output.append(". CD: " + move.getDuration() + "ms");
    
    String dps = "" + ((move.getDamage()*100000)/move.getDuration());
    
    if(dps.length()>2){
      dps = dps.substring(0, dps.length()-2) + "." + dps.substring(dps.length()-2);
    }else if (dps.length() == 2){
      dps = "0." + dps;
    }else{
      dps = "0.0" + dps;
    }
    
    output.append(". DPS: " + dps);
    
    if(move.getEnergyDelta() >= 0){
        output.append(". NRG: " + move.getEnergyDelta());
        String eps = "" + ((move.getEnergyDelta()*100000)/move.getDuration());
        
        if(eps.length()>2){
          eps = eps.substring(0, eps.length()-2) + "." + eps.substring(eps.length()-2);
        }else if (eps.length() == 2){
          eps = "0." + eps;
        }else{
          eps = "0.0" + eps;
        }
        
        output.append(". EPS: " + eps);
        
    }

    output.append(". WndSt: " + move.getDamageWindowStart());

    return output.toString();
  }
  
  public static void printMovesetDPS(FileWriter writer, GameMaster gameMaster, MovesetTable movesets) throws Exception{
    Hashtable<Integer, BasePokemon> pokemonTable = gameMaster.getPokemonTable();
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    
    Set<Integer> pokemonIds = pokemonTable.keySet();
    Set<Integer> moveIds = moveTable.keySet();
    
    SortedSet<Integer> pokemonIdsSorted = new TreeSet<Integer>();
    pokemonIdsSorted.addAll(pokemonIds);
    
    SortedSet<Integer> moveIdsSorted = new TreeSet<Integer>();
    moveIdsSorted.addAll(moveIds);
    
    writer.write(csvThis(new String[]{
        "DEX#",
        "Name",
        "Type1",
        "Type2",
        "Fast",
        "Fast Type",
        "Dmg",
        "CD(ms)",
        "NRG",
        "NRGPS",
        "STAB DPS",
        "Charge",
        "Charge Type",
        "Dmg",
        "CD(ms)",
        "NRG",
        "STAB WDPS",
        "BestDPS",
        "Attack Power",
        "Legacy"
    }));
    writer.write("\r\n");
    
    Hashtable<Integer, MovesetInfo> movesetsCopy = (Hashtable<Integer, MovesetInfo>)movesets.getMovesetInfoTable().clone();

    for(Integer index : pokemonIdsSorted){
      BasePokemon pokemon = pokemonTable.get(index);
      int[] quickMoves = pokemon.getQuickMoves();
      int[] chargeMoves = pokemon.getChargeMoves();
      for(int i = 0; i < quickMoves.length; i++){
        for(int j = 0; j < chargeMoves.length; j++){
          Move quick = moveTable.get(quickMoves[i]);
          Move charge = moveTable.get(chargeMoves[j]);
          MovesetInfo mi = new MovesetInfo(index, quick.getMoveId(), charge.getMoveId());
          movesetsCopy.remove(mi.getUniqueId());
          calculateDPSAndWrite(writer, gameMaster, pokemon, quick, charge, false);
        }
      }
    }
    
    //Do legacy moves
    Set<Integer> legacyMovesetKeys = movesetsCopy.keySet();
    for(Integer uniqueId : legacyMovesetKeys){
      MovesetInfo info = movesetsCopy.get(uniqueId);
      BasePokemon pokemon = pokemonTable.get(info.getPokemonId());
      Move quick = moveTable.get(info.getQuickMoveId());
      Move charge = moveTable.get(info.getChargeMoveId());
      calculateDPSAndWrite(writer, gameMaster, pokemon, quick, charge, true);
    }
  }
  
  private static void calculateDPSAndWrite(FileWriter writer, GameMaster gameMaster, BasePokemon pokemon, Move quick, Move charge, boolean isLegacy) throws Exception {
    
    double quickDPS = quick.getDamage()*1000/(double)quick.getDuration();
    if(isSTAB(pokemon, quick)){
      quickDPS *= gameMaster.getBattleSettings().getStabMultiplier();
    }
    double quickEPS = quick.getEnergyDelta()*1000/(double)quick.getDuration();
    double chargeDPS = charge.getDamage()*1000/(double)charge.getDuration();
    if(isSTAB(pokemon, charge)){
      chargeDPS *= gameMaster.getBattleSettings().getStabMultiplier();
    }
    double weaveDPS = quickDPS;
    if(charge.getEnergyDelta()==0){
      if(chargeDPS > weaveDPS){
        weaveDPS = chargeDPS;
      }
    }else if(quick.getEnergyDelta()==0){
      if(charge.getEnergyDelta()>=0){
        if(chargeDPS > weaveDPS){
          weaveDPS = chargeDPS;
        }
      }
    }else{
      int msPerCharge = (int)(((-charge.getEnergyDelta())/(double)quick.getEnergyDelta())*quick.getDuration());
      double quickDamagePerCharge = ((-charge.getEnergyDelta())/(double)quick.getEnergyDelta())*quick.getDamage();
      if(isSTAB(pokemon, quick)){
        quickDamagePerCharge *= gameMaster.getBattleSettings().getStabMultiplier();
      }
      double chargeDamage = charge.getDamage();
      if(isSTAB(pokemon, charge)){
        chargeDamage *= gameMaster.getBattleSettings().getStabMultiplier();
      }
      double totalDamagePerCycle = quickDamagePerCharge + chargeDamage;
      int totalMsPerCycle = msPerCharge + charge.getDuration();
      weaveDPS = totalDamagePerCycle*1000/totalMsPerCycle;
    }
    double maxDPS = weaveDPS>quickDPS?weaveDPS:quickDPS;
    double attackDPS = maxDPS * (pokemon.getBaseAttack()+15);
    writer.write(csvThis(new String[]{
        ""+pokemon.getPokemonId(),
        pokemon.getName(),
        gameMaster.getTypeTable().get(pokemon.getType()).getName(),
        pokemon.getType2()==0?"":gameMaster.getTypeTable().get(pokemon.getType2()).getName(),
        quick.getName(),
        gameMaster.getTypeTable().get(quick.getType()).getName(),
        ""+quick.getDamage(),
        ""+quick.getDuration(),
        ""+quick.getEnergyDelta(),
        ""+((int)(((int)(quickEPS*100))/100))+"."+(((int)(quickEPS*10))%10)+(((int)(quickEPS*100))%10),
        ""+((int)(((int)(quickDPS*100))/100))+"."+(((int)(quickDPS*10))%10)+(((int)(quickDPS*100))%10),
        charge.getName(),
        gameMaster.getTypeTable().get(charge.getType()).getName(),
        ""+charge.getDamage(),
        ""+charge.getDuration(),
        ""+(-charge.getEnergyDelta()),
        ""+((int)(((int)(weaveDPS*100))/100))+"."+(((int)(weaveDPS*10))%10)+(((int)(weaveDPS*100))%10),
        ""+((int)(((int)(maxDPS*100))/100))+"."+(((int)(maxDPS*10))%10)+(((int)(maxDPS*100))%10),
        ""+((int)(((int)(attackDPS*100))/100))+"."+(((int)(attackDPS*10))%10)+(((int)(attackDPS*100))%10),
        isLegacy?"Legacy":""
    }));
    writer.write("\r\n");
  }
  
  public static String csvThis(String[] args){
    StringBuffer output = new StringBuffer();
    output.append(args[0]);
    for(int i = 1; i < args.length; i++){
      output.append(","+args[i]);
    }
    return output.toString();
  }
  
  private static boolean isSTAB(BasePokemon pokemon, Move move){
    return (pokemon.getType() == move.getType()) || (pokemon.getType2() == move.getType());
  }
  
  public static void printTrainingSpreadsheetData(FileWriter movesetCombinationsOut, FileWriter pokemonStatsOut, FileWriter basicMovesOut, FileWriter chargeMovesOut, GameMaster gameMaster, MovesetTable movesets) throws Exception {
    printMovesetCombination(movesetCombinationsOut, gameMaster, movesets);
    printPokemonStats(pokemonStatsOut, gameMaster);
    printBasicMoves(basicMovesOut, gameMaster);
    printChargeMoves(chargeMovesOut, gameMaster);
  }
  
  public static void printMovesetCombination(FileWriter out, GameMaster gameMaster, MovesetTable movesets) throws Exception{
    Hashtable<Integer, BasePokemon> pokemonTable = gameMaster.getPokemonTable();
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    
    Set<Integer> movesetIds = movesets.getMovesetInfoTable().keySet();

    SortedSet<Integer> movesetIdsSorted = new TreeSet<Integer>();
    movesetIdsSorted.addAll(movesetIds);

    for(Integer index : movesetIdsSorted){
      MovesetInfo info = movesets.getMovesetInfoTable().get(index);
      BasePokemon pokemon = pokemonTable.get(info.getPokemonId());
      Move quick = moveTable.get(info.getQuickMoveId());
      Move charge = moveTable.get(info.getChargeMoveId());
      out.write(csvThis(new String[]{
          normalize(pokemon.getName()),
          normalize(quick.getName()),
          normalize(charge.getName())
      }));
      out.write("\r\n");
    }
  }
  
  public static void printPokemonStats(FileWriter out, GameMaster gameMaster) throws Exception {
    Hashtable<Integer, BasePokemon> pokemonTable = gameMaster.getPokemonTable();
    Hashtable<Integer, Type> typeTable = gameMaster.getTypeTable();
    
    Set<Integer> pokemonIds = pokemonTable.keySet();

    SortedSet<Integer> pokemonIdsSorted = new TreeSet<Integer>();
    pokemonIdsSorted.addAll(pokemonIds);

    for(Integer index : pokemonIdsSorted){
      BasePokemon pokemon = pokemonTable.get(index);
      out.write(csvThis(new String[]{
          normalize(pokemon.getName()),
          "" + pokemon.getPokemonId(),
          "" + pokemon.getBaseStamina(),
          "" + pokemon.getBaseAttack(),
          "" + pokemon.getBaseDefense(),
          normalize(typeTable.get(pokemon.getType()).getName()),
          pokemon.getType2()==0?"None":normalize(typeTable.get(pokemon.getType2()).getName())
      }));
      out.write("\r\n");
    }
  }
  
  public static void printBasicMoves(FileWriter out, GameMaster gameMaster) throws Exception {
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    Hashtable<Integer, Type> typeTable = gameMaster.getTypeTable();

    Set<Integer> moveIds = moveTable.keySet();

    SortedSet<Integer> moveIdsSorted = new TreeSet<Integer>();
    moveIdsSorted.addAll(moveIds);
    
    for(Integer index : moveIdsSorted){
      Move move = moveTable.get(index);
      if(move.getEnergyDelta()>0){
        String dps = "" + ((move.getDamage()*100000)/move.getDuration());
        
        if(dps.length()>2){
          dps = dps.substring(0, dps.length()-2) + "." + dps.substring(dps.length()-2);
        }else if (dps.length() == 2){
          dps = "0." + dps;
        }else{
          dps = "0.0" + dps;
        }

        String eps = "" + ((move.getEnergyDelta()*100000)/move.getDuration());
        
        if(eps.length()>2){
          eps = eps.substring(0, eps.length()-2) + "." + eps.substring(eps.length()-2);
        }else if (eps.length() == 2){
          eps = "0." + eps;
        }else{
          eps = "0.0" + eps;
        }
        
        String[] bonus = new String[5];
        int bonusIdx = 0;
        
        for(int i = 0; i < 18; i++){
          if(gameMaster.getTypeAdvantage()[move.getType()-1][i] > 1.1){
            bonus[bonusIdx++] = normalize(typeTable.get(i+1).getName());
          }
        }
        
        for(int i = bonusIdx; i < 5; i++){
          bonus[i] = "";
        }
        
        String[] malus = new String[7];
        int malusIdx = 0;
        
        for(int i = 0; i < 18; i++){
          if(gameMaster.getTypeAdvantage()[move.getType()-1][i] < 0.9){
            malus[malusIdx++] = normalize(typeTable.get(i+1).getName());
          }
        }
        
        for(int i = malusIdx; i < 7; i++){
          malus[i] = "";
        }
        

        out.write(csvThis(new String[]{
          "" + move.getMoveId(),
          normalize(move.getName()),
          normalize(typeTable.get(move.getType()).getName()),
          "" + move.getDamage(),
          "" + move.getDuration(),
          "" + move.getEnergyDelta(),
          eps,
          dps,
          bonus[0],
          bonus[1],
          bonus[2],
          bonus[3],
          bonus[4],
          malus[0],
          malus[1],
          malus[2],
          malus[3],
          malus[4],
          malus[5],
          malus[6]
        }));
        out.write("\r\n");
      }
    }
  }
  
  public static void printChargeMoves(FileWriter out, GameMaster gameMaster) throws Exception {
    Hashtable<Integer, Move> moveTable = gameMaster.getMoveTable();
    Hashtable<Integer, Type> typeTable = gameMaster.getTypeTable();

    Set<Integer> moveIds = moveTable.keySet();

    SortedSet<Integer> moveIdsSorted = new TreeSet<Integer>();
    moveIdsSorted.addAll(moveIds);
    
    for(Integer index : moveIdsSorted){
      Move move = moveTable.get(index);
      if(move.getEnergyDelta()<0){
        String[] bonus = new String[5];
        int bonusIdx = 0;
        
        for(int i = 0; i < 18; i++){
          if(gameMaster.getTypeAdvantage()[move.getType()-1][i] > 1.1){
            bonus[bonusIdx++] = normalize(typeTable.get(i+1).getName());
          }
        }
        
        for(int i = bonusIdx; i < 5; i++){
          bonus[i] = "";
        }
        
        String[] malus = new String[7];
        int malusIdx = 0;
        
        for(int i = 0; i < 18; i++){
          if(gameMaster.getTypeAdvantage()[move.getType()-1][i] < 0.9){
            malus[malusIdx++] = normalize(typeTable.get(i+1).getName());
          }
        }
        
        for(int i = malusIdx; i < 7; i++){
          malus[i] = "";
        }

        out.write(csvThis(new String[]{
          "" + move.getMoveId(),
          normalize(move.getName()),
          normalize(typeTable.get(move.getType()).getName()),
          "" + move.getDamage(),
          "" + move.getDuration(),
          "" + move.getDamageWindowStart(),
          "" + ((int)(move.getCriticalChance()*100)) + ".00%",
          "" + -move.getEnergyDelta(),
          bonus[0],
          bonus[1],
          bonus[2],
          bonus[3],
          bonus[4],
          malus[0],
          malus[1],
          malus[2],
          malus[3],
          malus[4],
          malus[5],
          malus[6]
        }));
        out.write("\r\n");
      }
    }
  }
  
  public static String normalize(String input){
    StringBuffer output = new StringBuffer();
    input = input.replaceAll("_", " ").toLowerCase();
    boolean previousSpace = true;
    for(int i = 0; i < input.length(); i++){
      if(input.charAt(i) == ' '){
        output.append(input.charAt(i));
        previousSpace = true;
      }else{
        if(previousSpace){
          output.append(Character.toUpperCase(input.charAt(i)));
        }else{
          output.append(input.charAt(i));
        }
        previousSpace = false;
      }
    }
    return output.toString();
  }
  
  public static void printMovesetsCSV(FileWriter out, MovesetTable movesets) throws Exception{
    Set<Integer> keys = movesets.getMovesetInfoTable().keySet();
    for(Integer key : keys){
      MovesetInfo info = movesets.getMovesetInfoTable().get(key);
      out.write(csvThis(new String[]{
          ""+info.getPokemonId(),
          ""+info.getQuickMoveId(),
          ""+info.getChargeMoveId()
      }));
      out.write("\r\n");
    }
  }
  
  public static void printFightResult(Writer out, FightResult result) throws Exception {
    CombatantResult att = result.getCombatant(0);
    CombatantResult def = result.getCombatant(1);
    out.write(MessageFormat.format("{0}({1}/{2}): {3}ms. Hp: {4}/{5}\r\n\r\n", 
        new Object[]{
          att.getPokemon().getBasePokemon().getName(), 
          att.getPokemon().getQuickMove().getName(), 
          att.getPokemon().getChargeMove().getName(),
          result.getTotalCombatTime(),
          att.getEndHp(),
          att.getStartHp()
        }));
    for(CombatResult results : result.getCombatResults()){
      out.write(MessageFormat.format("{0}: {1} - {2}" + (results.getAttackMove().getMoveId() == Move.DODGE_ID?"":" Dmg: {3}") + "\r\n", new Object[]{100000 - results.getCurrentTime(), results.getAttacker().getBasePokemon().getName(), results.getAttackMove().getName(), results.getDamage()}));
    }
    if(result instanceof MonteCarloFightResult){
      for(CombatResult results : ((MonteCarloFightResult)result).getWorstCombatResults()){
        out.write(MessageFormat.format("{0}: {1} - {2}" + (results.getAttackMove().getMoveId() == Move.DODGE_ID?"":" Dmg: {3}") + "\r\n", new Object[]{100000 - results.getCurrentTime(), results.getAttacker().getBasePokemon().getName(), results.getAttackMove().getName(), results.getDamage()}));
      }
    }
    
  }
}
