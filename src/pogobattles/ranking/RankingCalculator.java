package pogobattles.ranking;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.simulator.AttackSimulator;
import pogobattles.battle.simulator.CombatantResult;
import pogobattles.battle.simulator.FightResult;
import pogobattles.battle.simulator.Formulas;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;
import pogobattles.gamemaster.PrintUtil;

public class RankingCalculator {
  
  public static boolean OUTPUT_RANKING = false;
  
  public static void calculateAllPrestigeByAttackers(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int defLevel, boolean optimal) throws Exception {
    calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 15, 15, 15, 400, 15, 15, 15, defLevel, optimal);
  }

  public static void calculateAllPrestigeByAttackers(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, boolean optimal) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
      AttackStrategyType.DODGE_ALL,
      AttackStrategyType.DODGE_ALL2
    };
    Set<Integer> movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    List<MovesetInfo> attackerMovesetInfoList = new ArrayList<MovesetInfo>();
    for(Integer key: movesetsKeySet){
      if(attackers == null || attackers.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
        attackerMovesetInfoList.add(movesets.getMovesetInfoTable().get(key));
      }
    }

    movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    List<MovesetInfo> defenderMovesetInfoList = new ArrayList<MovesetInfo>();
    for(Integer key: movesetsKeySet){
      if(defenders == null || defenders.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
        defenderMovesetInfoList.add(movesets.getMovesetInfoTable().get(key));
      }
    }
    
    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    FileWriter lossesWriter = new FileWriter(outputFolder + "LossCounters.csv");
    lossesWriter.write(PrintUtil.csvThis(new String[]{
       "Pokemon",
       "QuickAttack",
       "ChargeAttack"
    }));
    for(AttackStrategyType strategy : strategies){
      lossesWriter.write(",Losses(" + strategy.name() + "),20/20 Cut-off(" + strategy.name() + "),30/30 Cut-off(" + strategy.name() +")");
    }
    lossesWriter.write("\r\n");
    for(MovesetInfo attackerMovesetInfo : attackerMovesetInfoList){
      BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
      Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
      Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
      String fileName = attackerBase.getName() + "." + attackerQuick.getName() + "." + attackerCharge.getName() + ".csv";
      System.out.println("Calculating: " + fileName);
      //Battle Logs output:
      /*
      String logsPath = outputFolder + "Logs\\";
      logsPath.mkdirs();
      FileWriter logWriter = new FileWriter(logsPath + fileName + ".log");
      */
      FileWriter writer = new FileWriter(outputFolder + fileName);
      writer.write(PrintUtil.csvThis(new String[]{
          "Defender",
          "QuickMove",
          "ChargeMove",
          "DefCP",
          "AttCP",
          "AttLevel",
          "PrestigeGained",
          "CombatTime",
          "HpLeft",
          "TotalHp",
          "HpLeft%",
          "DefHpLeft",
          "DefTotHp",
          "DefHp%",
          "Power%",
          "DPS",
          "Strategy"
      }));
      writer.write("\r\n");
      int[] losses = new int[strategies.length];
      int[] cutoff20_20 = new int[strategies.length];
      int[] cutoff30_30 = new int[strategies.length];
      for(MovesetInfo defenderMovesetInfo : defenderMovesetInfoList){
        BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
        Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
        Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
        Pokemon defender = creator.createPokemon(defenderBase, defLevel, defIvAtt, defIvDef, defIvSta, defenderQuick, defenderCharge);
        Pokemon attacker = creator.createPokemon(attackerBase, defender.getCp()/2, maxAttLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
        for(int i = 0; i < strategies.length; i++){
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i]);
          int currentLevel = attacker.getLevel();
          while(!result.isWin() && currentLevel < maxAttLevel && optimal){
            currentLevel +=5;
            Pokemon tryAgainAttacker = creator.createPokemon(attackerBase, currentLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
            result = simulator.calculateAttackDPS(tryAgainAttacker, defender, strategies[i]);
          }
          if(result.isWin()){
            double healthPercent = (((double)result.getCombatant(0).getEndHp()*100)/(double)result.getCombatant(0).getStartHp());
            int secondsRemaining = (Formulas.MAX_COMBAT_TIME_MS - result.getTotalCombatTime())/1000;
            if(healthPercent < 20 || secondsRemaining < 20){
              cutoff20_20[i]++;
              cutoff30_30[i]++;
            }else if(healthPercent < 30 || secondsRemaining < 30){
              cutoff30_30[i]++;
            }
          }else{
            losses[i]++;
            cutoff20_20[i]++;
            cutoff30_30[i]++;
          }
          // Debug fight to log:
          /*
          if(result.isWin() 
              //&& attacker.getBasePokemon().getName().equals("HAUNTER")
              //&& attacker.getQuickMove().getName().equals("LICK")
              //&& attacker.getChargeMove().getName().equals("SLUDGE_BOMB")
              && defender.getBasePokemon().getName().equals("BLISSEY") //Log only defender BLISSEY fights
              //&& defender.getQuickMove().getName().equals("POUND")
              //&& defender.getChargeMove().getName().equals("DAZZLING_GLEAM")
              ){
            //PrintUtil.printFightResult(logWriter, result); //log to file
            //PrintUtil.printFightResult(new OutputStreamWriter(System.out), result); //log to System.out.
            logWriter.write("\r\n");
            logWriter.write("\r\n");
          }
          */
          writeDefenderFightResult(writer, result);
        }
        writer.flush();
      }
      writer.close();
      lossesWriter.write(PrintUtil.csvThis(new String[]{
          attackerBase.getName(),
          attackerQuick.getName(),
          attackerCharge.getName()
      }));
      
      for(int i = 0; i < strategies.length; i++){
        lossesWriter.write("," + losses[i] + "," + cutoff20_20[i] + "," + cutoff30_30[i]);
      }
      lossesWriter.write("\r\n");
    }
    lossesWriter.flush();
    lossesWriter.close();
  }
  
  public static void calculateAllPrestige(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int defLevel, boolean optimal) throws Exception {
    calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, defLevel, optimal);
  }

  public static void calculateAllPrestige(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, boolean optimal) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
      AttackStrategyType.DODGE_ALL,
      AttackStrategyType.DODGE_ALL2,
      AttackStrategyType.DODGE_ALL3,
      AttackStrategyType.DODGE_SPECIALS,
      AttackStrategyType.DODGE_SPECIALS2,
      AttackStrategyType.DODGE_SPECIALS3,
      AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE
    };
    Set<Integer> movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    List<MovesetInfo> movesetInfoList = new ArrayList<MovesetInfo>();
    for(Integer key: movesetsKeySet){
      if(defenders.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
        movesetInfoList.add(movesets.getMovesetInfoTable().get(key));
      }
    }
    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    for(MovesetInfo defenderMovesetInfo : movesetInfoList){
      BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
      Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
      Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
      Pokemon defender = creator.createPokemon(defenderBase, defLevel, defIvAtt, defIvDef, defIvSta, defenderQuick, defenderCharge);
      String fileName = defenderBase.getName() + "." + defenderQuick.getName() + "." + defenderCharge.getName() + ".csv";
      System.out.println("Calculating: " + fileName);
      //Battle Logs output:
      /*
      String logsPath = outputFolder + "Logs\\";
      logsPath.mkdirs();
      FileWriter logWriter = new FileWriter(logsPath + fileName + ".log");
      */
      FileWriter writer = new FileWriter(outputFolder + fileName);
      writer.write(PrintUtil.csvThis(new String[]{
          "Pokemon",
          "QuickMove",
          "ChargeMove",
          "CP",
          "Level",
          "PrestigeGained",
          "CombatTime",
          "HpLeft",
          "TotalHp",
          "HpLeft%",
          "DefCP",
          "DefLevel",
          "DefHpLeft",
          "DefTotHp",
          "DefHp%",
          "Power%",
          "DPS",
          "Strategy"
      }));
      writer.write("\r\n");
      for(Integer key: movesetsKeySet){
        MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
        BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
        Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
        Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
        Pokemon attacker = creator.createPokemon(attackerBase, defender.getCp()/2, maxAttLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
        for(int i = 0; i < strategies.length; i++){
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i]);
          int currentLevel = attacker.getLevel();
          while(!result.isWin() && currentLevel < maxAttLevel && optimal){
            currentLevel +=5;
            Pokemon tryAgainAttacker = creator.createPokemon(attackerBase, currentLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
            result = simulator.calculateAttackDPS(tryAgainAttacker, defender, strategies[i]);
          }
          // Debug fight to log:
          /*
          if(result.isWin() 
              //&& attacker.getBasePokemon().getName().equals("HAUNTER")
              //&& attacker.getQuickMove().getName().equals("LICK")
              //&& attacker.getChargeMove().getName().equals("SLUDGE_BOMB")
              && defender.getBasePokemon().getName().equals("BLISSEY") //Log only defender BLISSEY fights
              //&& defender.getQuickMove().getName().equals("POUND")
              //&& defender.getChargeMove().getName().equals("DAZZLING_GLEAM")
              ){
            //PrintUtil.printFightResult(logWriter, result); //log to file
            //PrintUtil.printFightResult(new OutputStreamWriter(System.out), result); //log to System.out.
            logWriter.write("\r\n");
            logWriter.write("\r\n");
          }
          */
          writeFightResult(writer, result);
        }
        writer.flush();
      }
      writer.close();
    }
  }
  
  public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel) throws Exception {
    calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel);
  }

  public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int attLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
        AttackStrategyType.DODGE_ALL,
        AttackStrategyType.DODGE_ALL2,
        AttackStrategyType.DODGE_ALL3,
        AttackStrategyType.DODGE_SPECIALS,
        AttackStrategyType.DODGE_SPECIALS2,
        AttackStrategyType.DODGE_SPECIALS3,
        AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE
    };
    Set<Integer> movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    List<MovesetInfo> movesetInfoList = new ArrayList<MovesetInfo>();
    for(Integer key: movesetsKeySet){
      if(defenders.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
        movesetInfoList.add(movesets.getMovesetInfoTable().get(key));
      }
    }
    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    for(MovesetInfo defenderMovesetInfo : movesetInfoList){
      BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
      Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
      Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
      Pokemon defender = creator.createPokemon(defenderBase, defLevel, defIvAtt, defIvDef, defIvSta, defenderQuick, defenderCharge);
      String fileName = defenderBase.getName() + "." + defenderQuick.getName() + "." + defenderCharge.getName() + ".csv";
      System.out.println("Calculating: " + fileName);
      FileWriter writer = new FileWriter(outputFolder + fileName);
      writer.write(PrintUtil.csvThis(new String[]{
          "Pokemon",
          "QuickMove",
          "ChargeMove",
          "CP",
          "Level",
          "PrestigeGained",
          "CombatTime",
          "HpLeft",
          "TotalHp",
          "HpLeft%",
          "DefCP",
          "DefLevel",
          "DefHpLeft",
          "DefTotHp",
          "DefHp%",
          "Power%",
          "DPS",
          "Strategy"
      }));
      writer.write("\r\n");
      for(Integer key: movesetsKeySet){
        MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
        BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
        Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
        Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
        Pokemon attacker = creator.createPokemon(attackerBase, attLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
        for(int i = 0; i < strategies.length; i++){
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i]);
          writeFightResult(writer, result);
        }
        writer.flush();
      }
      writer.close();
    }
  }

  public static void calculateAllPrestigeLevelsRange(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int minDefLevel, int maxDefLevel, boolean optimal) throws Exception {
    calculateAllPrestigeLevelsRange(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, minDefLevel, maxDefLevel, optimal);
  }

  public static void calculateAllPrestigeLevelsRange(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int minDefLevel, int maxDefLevel, boolean optimal) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
      AttackStrategyType.DODGE_ALL,
      AttackStrategyType.DODGE_ALL2
      //AttackStrategyType.DODGE_ALL3,
      //AttackStrategyType.DODGE_SPECIALS,
      //AttackStrategyType.DODGE_SPECIALS2,
      //AttackStrategyType.DODGE_SPECIALS3,
      //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE
    };
    Set<Integer> movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    List<MovesetInfo> movesetInfoList = new ArrayList<MovesetInfo>();
    for(Integer key: movesetsKeySet){
      if(defenders.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
        movesetInfoList.add(movesets.getMovesetInfoTable().get(key));
      }
    }
    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
    movesetsKeySet = movesets.getMovesetInfoTable().keySet();
    for(MovesetInfo defenderMovesetInfo : movesetInfoList){
      BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
      Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
      Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
      String fileName = defenderBase.getName() + "." + defenderQuick.getName() + "." + defenderCharge.getName() + ".csv";
      FileWriter writer = new FileWriter(outputFolder + fileName);
      writer.write(PrintUtil.csvThis(new String[]{
          "Pokemon",
          "QuickMove",
          "ChargeMove",
          "CP",
          "Level",
          "PrestigeGained",
          "CombatTime",
          "HpLeft",
          "TotalHp",
          "HpLeft%",
          "DefCp",
          "DefLevel",
          "DefHpLeft",
          "DefTotHp",
          "DefHp%",
          "Power%",
          "DPS",
          "Strategy"
      }));
      writer.write("\r\n");
      System.out.println("Calculating: " + fileName);
      int currentDefLevel = minDefLevel;
      while(currentDefLevel <= maxDefLevel){
        Pokemon defender = creator.createPokemon(defenderBase, currentDefLevel, defIvAtt, defIvDef, defIvSta, defenderQuick, defenderCharge);
        //Battle Logs output:
        /*
        String logsPath = outputFolder + "Logs\\";
        logsPath.mkdirs();
        FileWriter logWriter = new FileWriter(logsPath + fileName + ".log");
        */
        for(Integer key: movesetsKeySet){
          MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
          BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
          if(!attackerBase.getName().equalsIgnoreCase("Ditto")){
            Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
            Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
            Pokemon attacker = creator.createPokemon(attackerBase, defender.getCp()/2, maxAttLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
            for(int i = 0; i < strategies.length; i++){
              FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i]);
              int currentLevel = attacker.getLevel();
              while(!result.isWin() && currentLevel < maxAttLevel && optimal){
                currentLevel +=5;
                Pokemon tryAgainAttacker = creator.createPokemon(attackerBase, currentLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
                result = simulator.calculateAttackDPS(tryAgainAttacker, defender, strategies[i]);
              }
              // Debug fight to log:
              /*
              if(result.isWin() 
                  //&& attacker.getBasePokemon().getName().equals("HAUNTER")
                  //&& attacker.getQuickMove().getName().equals("LICK")
                  //&& attacker.getChargeMove().getName().equals("SLUDGE_BOMB")
                  && defender.getBasePokemon().getName().equals("BLISSEY") //Log only defender BLISSEY fights
                  //&& defender.getQuickMove().getName().equals("POUND")
                  //&& defender.getChargeMove().getName().equals("DAZZLING_GLEAM")
                  ){
                //PrintUtil.printFightResult(logWriter, result); //log to file
                //PrintUtil.printFightResult(new OutputStreamWriter(System.out), result); //log to System.out.
                logWriter.write("\r\n");
                logWriter.write("\r\n");
              }
              */
              writeFightResult(writer, result);
            }
            writer.flush();
          }
        }
        currentDefLevel += 5;
      }
      writer.close();
    }
  }
  
  private static void writeFightResult(FileWriter writer, FightResult result) throws Exception {
    CombatantResult attackerResult = result.getCombatant(0);
    Pokemon attacker = attackerResult.getPokemon();
    CombatantResult defenderResult = result.getCombatant(1);
    Pokemon defender = defenderResult.getPokemon();
    double hpLeftPercent = (attackerResult.getEndHp()<1?0:((double)attackerResult.getEndHp()*100)/attackerResult.getStartHp());
    writer.write(PrintUtil.csvThis(new String[]{
        attacker.getBasePokemon().getName(),
        attacker.getQuickMove().getName(),
        attacker.getChargeMove().getName(),
        "" + attackerResult.getCp(),
        "" + ((double)attacker.getLevel())/10,
        "" + result.getPrestige(),
        "" + result.getTotalCombatTime(),
        "" + attackerResult.getEndHp(),
        "" + attackerResult.getStartHp(),
        "" + (attackerResult.getEndHp()<1?"0":((double)attackerResult.getEndHp()*100)/attackerResult.getStartHp()),
        "" + defenderResult.getCp(),
        "" + ((double)defender.getLevel())/10,
        "" + defenderResult.getEndHp(),
        "" + defenderResult.getStartHp(),
        "" + (defenderResult.getEndHp()<1?"0":((double)defenderResult.getEndHp()*100)/defenderResult.getStartHp()),
        (result.getPower()*100) + "",
        "" + attackerResult.getDps(),
        attackerResult.getStrategy().name()
    }));
    if(OUTPUT_RANKING){
      writer.write(",");
      double timePercent =((double)Formulas.MAX_COMBAT_TIME_MS-result.getTotalCombatTime())/Formulas.MAX_COMBAT_TIME_MS;
      double timeModifier = (1.0/(((-timePercent)+(75.0/50.0))*1.25))-0.5;
      double healthModifier = (1/(((-(hpLeftPercent/100))-(1/50))*10))+1.1;
      writer.write(PrintUtil.csvThis(new String[]{
          "" + timePercent,
          "" + timeModifier,
          "" + healthModifier,
          "" + timeModifier*healthModifier
      }));
    }
    writer.write("\r\n");
  }

  private static void writeDefenderFightResult(FileWriter writer, FightResult result) throws Exception {
    CombatantResult attackerResult = result.getCombatant(0);
    Pokemon attacker = attackerResult.getPokemon();
    CombatantResult defenderResult = result.getCombatant(1);
    Pokemon defender = defenderResult.getPokemon();
    writer.write(PrintUtil.csvThis(new String[]{
        defender.getBasePokemon().getName(),
        defender.getQuickMove().getName(),
        defender.getChargeMove().getName(),
        "" + defender.getCp(),
        "" + attackerResult.getCp(),
        "" + ((double)attacker.getLevel())/10,
        "" + result.getPrestige(),
        "" + result.getTotalCombatTime(),
        "" + attackerResult.getEndHp(),
        "" + attackerResult.getStartHp(),
        "" + (attackerResult.getEndHp()<1?"0":((double)attackerResult.getEndHp()*100)/attackerResult.getStartHp()),
        "" + defenderResult.getEndHp(),
        "" + defenderResult.getStartHp(),
        "" + (defenderResult.getEndHp()<1?"0":((double)defenderResult.getEndHp()*100)/defenderResult.getStartHp()),
        (result.getPower()*100) + "",
        "" + attackerResult.getDps(),
        attackerResult.getStrategy().name()
    }));
    writer.write("\r\n");
  }
}
