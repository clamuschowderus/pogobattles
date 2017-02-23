package pogobattles.ranking;

import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.simulator.AttackSimulator;
import pogobattles.battle.simulator.CombatantResult;
import pogobattles.battle.simulator.FightResult;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;
import pogobattles.gamemaster.PrintUtil;

public class RankingCalculator {
  public static void calculateAllPrestige(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, boolean optimal) throws Exception {
    calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, null, 15, 15, 15, 400, 15, 15, 15, 400, optimal);
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
  
  public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets) throws Exception {
    calculateAll(outputFolder, simulator, gameMaster, movesets, null, 15, 15, 15, 400, 15, 15, 15, 400);
  }
  
  public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel) throws Exception {
    calculateAll(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel);
  }

  public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel) throws Exception {
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
      //System.out.println("Calculating: " + fileName);
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
        Pokemon attacker = creator.createPokemon(attackerBase, maxAttLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
        for(int i = 0; i < strategies.length; i++){
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i]);
          writeFightResult(writer, result);
        }
        writer.flush();
      }
      writer.close();
    }
  }

  private static void writeFightResult(FileWriter writer, FightResult result) throws Exception {
    CombatantResult attackerResult = result.getCombatant(0);
    Pokemon attacker = attackerResult.getPokemon();
    CombatantResult defenderResult = result.getCombatant(1);
    Pokemon defender = defenderResult.getPokemon();
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
