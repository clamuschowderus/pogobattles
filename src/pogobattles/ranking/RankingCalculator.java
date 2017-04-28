package pogobattles.ranking;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.data.PokemonDataCreator;
import pogobattles.battle.simulator.CombatantResult;
import pogobattles.battle.simulator.FightResult;
import pogobattles.battle.simulator.FightSimulator;
import pogobattles.battle.simulator.Formulas;
import pogobattles.battle.simulator.MonteCarloFightResult;
import pogobattles.battle.simulator.MonteCarloFightSimulator;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;
import pogobattles.gamemaster.PrintUtil;

public class RankingCalculator {
  
  public static final boolean OUTPUT_RANKING = true;
  
  public static final String CRITERIA_POTION_EFFICIENCY = "POTIONS";
  
  public static void calculateAllPrestigeByAttackers(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int defLevel, boolean optimal) throws Exception {
    calculateAllPrestigeByAttackers(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 15, 15, 15, 400, 15, 15, 15, defLevel, optimal);
  }

  public static void calculateAllPrestigeByAttackers(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, boolean optimal) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
        //AttackStrategyType.DODGE_ALL,
        //AttackStrategyType.DODGE_ALL2,
        //AttackStrategyType.DODGE_ALL3,
        //AttackStrategyType.DODGE_SPECIALS,
        //AttackStrategyType.DODGE_SPECIALS2,
        //AttackStrategyType.DODGE_SPECIALS3,
        //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
        AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
        AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
        AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
        AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN,
        AttackStrategyType.DODGE_SPECIALS_HUMAN   
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
      writeHeader(writer, simulator instanceof MonteCarloFightSimulator);
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
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i], AttackStrategyType.DEFENSE_RANDOM);
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
          writeFightResultDetailed(writer, result);
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
  
  public static void calculateAllPrestige(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int defLevel, boolean optimal) throws Exception {
    calculateAllPrestige(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, defLevel, optimal);
  }

  public static void calculateAllPrestige(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, boolean optimal) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
      //AttackStrategyType.DODGE_ALL,
      //AttackStrategyType.DODGE_ALL2,
      //AttackStrategyType.DODGE_ALL3,
      //AttackStrategyType.DODGE_SPECIALS,
      //AttackStrategyType.DODGE_SPECIALS2,
      //AttackStrategyType.DODGE_SPECIALS3,
      //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
      AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
      AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
      AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
      AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN,
      AttackStrategyType.DODGE_SPECIALS_HUMAN   
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
      writeHeader(writer, simulator instanceof MonteCarloFightSimulator);
      for(Integer key: movesetsKeySet){
        MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
        BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
        Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
        Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
        Pokemon attacker = creator.createPokemon(attackerBase, defender.getCp()/2, maxAttLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
        for(int i = 0; i < strategies.length; i++){
          FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i], AttackStrategyType.DEFENSE_RANDOM);
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
          writeFightResultDetailed(writer, result);
        }
        writer.flush();
      }
      writer.close();
    }
  }
  
  public static void calculateAll(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel) throws Exception {
    calculateAll(outputFolder, simulator, gameMaster, movesets, null, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel);
  }

  public static void calculateAll(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int attLevel, int defLevel) throws Exception {
    calculateAll(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel);
  }
  
  public static void calculateAll(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int ivAtt, int ivDef, int ivSta, int attLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
        AttackStrategyType.DODGE_ALL,
        AttackStrategyType.DODGE_ALL2,
        AttackStrategyType.DODGE_SPECIALS,
        AttackStrategyType.DODGE_SPECIALS2,
        //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
        //AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
        //AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
        //AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
        //AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN,
        //AttackStrategyType.DODGE_ALL_MAX_RISK_HUMAN,
        //AttackStrategyType.DODGE_SPECIALS_HUMAN,
        //AttackStrategyType.DODGE_SPECIALS_NINJA
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
      writeHeader(writer, simulator instanceof MonteCarloFightSimulator);
      for(Integer key: movesetsKeySet){
        if(attackers == null || 
            attackers.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
          MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
          BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
          Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
          Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
          Pokemon attacker = creator.createPokemon(attackerBase, attLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
          for(int i = 0; i < strategies.length; i++){
            FightResult result = simulator.calculateAttackDPS(attacker, defender, strategies[i], AttackStrategyType.DEFENSE_RANDOM);
            writeFightResultDetailed(writer, result);
          }
          writer.flush();
        }
      }
      writer.close();
    }
  }

  public static void calculateAllBest(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel, String criteria) throws Exception {
    calculateAllBest(outputFolder, simulator, gameMaster, movesets, null, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel, criteria);
  }

  public static void calculateAllBest(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int attLevel, int defLevel, String criteria) throws Exception {
    calculateAllBest(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel, criteria);
  }
	  
  public static void calculateAllBest(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int ivAtt, int ivDef, int ivSta, int attLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, String criteria) throws Exception {
	    AttackStrategyType[] strategies = new AttackStrategyType[]{
	        //AttackStrategyType.DODGE_ALL
	        //AttackStrategyType.DODGE_ALL2,
	        //AttackStrategyType.DODGE_SPECIALS,
	        //AttackStrategyType.DODGE_SPECIALS2,
	        //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
	        AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
	        AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
	        AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
	        AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN,
	        AttackStrategyType.DODGE_ALL_MAX_RISK_HUMAN,
	        AttackStrategyType.DODGE_SPECIALS_HUMAN,
	        AttackStrategyType.DODGE_SPECIALS_NINJA
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
	      writeHeader(writer, simulator instanceof MonteCarloFightSimulator);
	      for(Integer key: movesetsKeySet){
	        if(attackers == null || 
	            attackers.contains(PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName()))){
	          MovesetInfo attackerMovesetInfo = movesets.getMovesetInfoTable().get(key);
	          BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
	          Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
	          Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
	          Pokemon attacker = creator.createPokemon(attackerBase, attLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
	          FightResult result = null;
	          for(int i = 0; i < strategies.length; i++){
	            FightResult newResult = simulator.calculateAttackDPS(attacker, defender, strategies[i], AttackStrategyType.DEFENSE_RANDOM);
	            if(result == null){
	            	result = newResult;
	            }else{
	            	boolean evaluated = false;
	            	if(newResult instanceof MonteCarloFightResult){
	            		MonteCarloFightResult mcResult = (MonteCarloFightResult)result;
	            		MonteCarloFightResult mcNewResult = (MonteCarloFightResult)newResult;
	            		if(
	            				(mcNewResult.getWinRatio() > mcResult.getWinRatio()) ){
	            			result = newResult;
	            			evaluated = true;
	            		}else if(mcNewResult.getWinRatio() < mcResult.getWinRatio()){
	            			evaluated = true;
	            		}
	            	}
	            	if(!evaluated){
	            		if(criteria != null && criteria.equals(RankingCalculator.CRITERIA_POTION_EFFICIENCY)){
	            			CombatantResult resAttacker = result.getCombatant(0);
	            			CombatantResult newResAttacker = newResult.getCombatant(0);
	            			int resHPLost = Math.max(0,  resAttacker.getStartHp() - resAttacker.getEndHp());
	            			int newResHPLost = Math.max(0,  newResAttacker.getStartHp() - newResAttacker.getEndHp());
	            			if(resHPLost > newResHPLost){
	            				result = newResult;
	            			}
	            		}else if(newResult.getTotalPower() > result.getTotalPower()){
	            			result = newResult;
	            		}
	            	}
	            }
	          }
              writeFightResultDetailed(writer, result);
	          writer.flush();
	        }
	      }
	      writer.close();
	    }
  }

  public static void calculateConsolidatedResultsByDefender(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel, String criteria) throws Exception {
	  calculateConsolidatedResultsByDefender(outputFolder, simulator, gameMaster, movesets, null, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel, criteria);
  }

  public static void calculateConsolidatedResultsByDefender(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int attLevel, int defLevel, String criteria) throws Exception {
	  calculateConsolidatedResultsByDefender(outputFolder, simulator, gameMaster, movesets, attackers, defenders, 15, 15, 15, attLevel, 15, 15, 15, defLevel, criteria);
  }
		  
  public static void calculateConsolidatedResultsByDefender(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> attackers, List<String> defenders, int ivAtt, int ivDef, int ivSta, int attLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, String criteria) throws Exception {
	    Hashtable<String, List<MovesetInfo>> defenderMovesetTable = new Hashtable<String, List<MovesetInfo>>();
	    Hashtable<String, List<MovesetInfo>> attackerMovesetTable = new Hashtable<String, List<MovesetInfo>>();
	    AttackStrategyType[] strategies = new AttackStrategyType[]{
	        //AttackStrategyType.DODGE_ALL
	        //AttackStrategyType.DODGE_ALL2,
	        AttackStrategyType.DODGE_SPECIALS,
	        //AttackStrategyType.DODGE_SPECIALS2,
	        //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
	        //AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
	        //AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
	        //AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
	        //AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN,
	        //AttackStrategyType.DODGE_ALL_MAX_RISK_HUMAN,
	        //AttackStrategyType.DODGE_SPECIALS_HUMAN,
	        //AttackStrategyType.DODGE_SPECIALS_NINJA
	    };
	    Set<Integer> movesetsKeySet = movesets.getMovesetInfoTable().keySet();
	    for(Integer key: movesetsKeySet){
	      String pokemonName = PrintUtil.normalize(gameMaster.getPokemonTable().get(key/1000000).getName());
	      if(defenders.contains(pokemonName)){
	  	    List<MovesetInfo> movesetInfoList = defenderMovesetTable.get(pokemonName);
	  	    if(movesetInfoList == null){
	  	    	movesetInfoList = new ArrayList<MovesetInfo>();
	  	    	defenderMovesetTable.put(pokemonName, movesetInfoList);
	  	    }
	        movesetInfoList.add(movesets.getMovesetInfoTable().get(key));
	      }
	      if(attackers.contains(pokemonName)){
		  	    List<MovesetInfo> movesetInfoList = attackerMovesetTable.get(pokemonName);
		  	    if(movesetInfoList == null){
		  	    	movesetInfoList = new ArrayList<MovesetInfo>();
		  	    	attackerMovesetTable.put(pokemonName, movesetInfoList);
		  	    }
		        movesetInfoList.add(movesets.getMovesetInfoTable().get(key));
	      }
	    }
	    PokemonDataCreator creator = new PokemonDataCreator(gameMaster);
	    movesetsKeySet = movesets.getMovesetInfoTable().keySet();
	    Set<String> defenderNames = defenderMovesetTable.keySet();
	    for(String defenderName : defenderNames){
	      List<MovesetInfo> defenderMovesets = defenderMovesetTable.get(defenderName);
		  Set<String> attackerNames = attackerMovesetTable.keySet();
          String fileName = defenderName + ".ALL.ALL.csv";
          System.out.println("Calculating: " + fileName);
          FileWriter writer = new FileWriter(outputFolder + fileName);
          writer.write(PrintUtil.csvThis(
        		  new String[]{
        			"Attacker",
        			"FastMove",
        			"SpecialMove",
        			"Power",
        			"CombatTime",
        			"HealthRemaining",
        			"Losses",
        			"Timeouts"
        		  }
          ));
          writer.write("\r\n");
		  for(String attackerName : attackerNames){
		    List<MovesetInfo> attackerMovesets = attackerMovesetTable.get(attackerName);
		    for(MovesetInfo attackerMovesetInfo : attackerMovesets){
              BasePokemon attackerBase = gameMaster.getPokemonTable().get(attackerMovesetInfo.getPokemonId());
              Move attackerQuick = gameMaster.getMoveTable().get(attackerMovesetInfo.getQuickMoveId());
              Move attackerCharge = gameMaster.getMoveTable().get(attackerMovesetInfo.getChargeMoveId());
              Pokemon attacker = creator.createPokemon(attackerBase, attLevel, ivAtt, ivDef, ivSta, attackerQuick, attackerCharge);
              List<FightResult> resultList = new ArrayList<FightResult>();
	          for(MovesetInfo defenderMovesetInfo : defenderMovesets){
	            BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
	            Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
	            Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
	            Pokemon defender = creator.createPokemon(defenderBase, defLevel, defIvAtt, defIvDef, defIvSta, defenderQuick, defenderCharge);
                FightResult result = null;
	            for(int i = 0; i < strategies.length; i++){
	              FightResult newResult = simulator.calculateAttackDPS(attacker, defender, strategies[i], AttackStrategyType.DEFENSE);
	              if(result == null){
	                result = newResult;
	              }else{
	            	boolean evaluated = false;
	            	if(newResult instanceof MonteCarloFightResult){
	            	  MonteCarloFightResult mcResult = (MonteCarloFightResult)result;
	            	  MonteCarloFightResult mcNewResult = (MonteCarloFightResult)newResult;
	            	  if(
	            			(mcNewResult.getWinRatio() > mcResult.getWinRatio()) ){
	            		result = newResult;
	            		evaluated = true;
	            	  }else if(mcNewResult.getWinRatio() < mcResult.getWinRatio()){
	            		evaluated = true;
	            	  }
	            	}
	            	if(!evaluated){
	            	  if(criteria != null && criteria.equals(RankingCalculator.CRITERIA_POTION_EFFICIENCY)){
	            		CombatantResult resAttacker = result.getCombatant(0);
	            		CombatantResult newResAttacker = newResult.getCombatant(0);
	            		int resHPLost = Math.max(0,  resAttacker.getStartHp() - resAttacker.getEndHp());
	            		int newResHPLost = Math.max(0,  newResAttacker.getStartHp() - newResAttacker.getEndHp());
	            		if(resHPLost > newResHPLost){
	            			result = newResult;
	            		}
	            	  }else if(newResult.getTotalPower() > result.getTotalPower()){
	            	    result = newResult;
	            	  }
	            	}
	              }
	            }
	            resultList.add(result);
	          }
	          double averagePower = 1;
	          int averageCombatTime = 0;
	          int averageHealthRemaining = 0;
	          int numResults = resultList.size();
	          int timeouts = 0;
	          int losses = 0;
	          for(int i = 0; i < numResults; i++){
	        	  FightResult result = resultList.get(i);
        		  averagePower *= result.getPower();
        		  averageCombatTime += result.getTotalCombatTime();
        		  int healthRemaining = result.getCombatant(0).getEndHp();
        		  averageHealthRemaining += healthRemaining;
        		  if(result.getTotalCombatTime() == 100000){
        			  timeouts++;
        			  losses++;
        		  }else{
        			  if(healthRemaining <= 0){
        				  losses++;
        			  }
        		  }
	          }
	          CombatantResult attackerResult = resultList.get(0).getCombatant(0);
	          averageCombatTime /= numResults;
	          averageHealthRemaining /= numResults;
	          double healthRemainingPct = ((double)averageHealthRemaining)/((double)attackerResult.getStartHp());
	          averagePower = Math.pow(averagePower, ((double)1)/((double)numResults));
	          writer.write(PrintUtil.csvThis(
	        		  new String[]{
	        			attackerResult.getPokemon().getBasePokemon().getName(),
	        			attackerResult.getPokemon().getQuickMove().getName(),
	        			attackerResult.getPokemon().getChargeMove().getName(),
	        			"" + ((int)(averagePower*100)),
	        			"" + averageCombatTime,
	        			"" + ((int)(healthRemainingPct*100)),
	        			"" + losses,
	        			"" + timeouts
	        		  }
	          ));
	          writer.write("\r\n");
	        }
	        writer.flush();
	      }
	      writer.close();
	    }
  }

  public static void calculateAllPrestigeLevelsRange(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int minDefLevel, int maxDefLevel, boolean optimal) throws Exception {
    calculateAllPrestigeLevelsRange(outputFolder, simulator, gameMaster, movesets, defenders, 15, 15, 15, 400, 15, 15, 15, minDefLevel, maxDefLevel, optimal);
  }

  public static void calculateAllPrestigeLevelsRange(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int minDefLevel, int maxDefLevel, boolean optimal) throws Exception {
    calculateAllPrestigeLevelsRange(outputFolder, simulator, gameMaster, movesets, defenders, ivAtt, ivDef, ivSta, maxAttLevel, defIvAtt, defIvDef, defIvSta, minDefLevel, maxDefLevel, optimal, 5);
  }  
  
  public static void calculateAllPrestigeLevelsRange(String outputFolder, FightSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int minDefLevel, int maxDefLevel, boolean optimal, int levelIncrement) throws Exception {
    AttackStrategyType[] strategies = new AttackStrategyType[]{
      //AttackStrategyType.DODGE_ALL,
      //AttackStrategyType.DODGE_ALL2,
      //AttackStrategyType.DODGE_ALL3,
      //AttackStrategyType.DODGE_SPECIALS,
      //AttackStrategyType.DODGE_SPECIALS2,
      //AttackStrategyType.DODGE_SPECIALS3,
      //AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE,
      //AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN,
      //AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN,
      //AttackStrategyType.DODGE_ALL_RISKY_HUMAN,
      //AttackStrategyType.DODGE_SPECIALS_HUMAN,
      AttackStrategyType.DODGE_RANDOM_HUMAN,
      AttackStrategyType.DODGE_RANDOM_HUMAN_SPECIALS
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
    Hashtable<String, FileWriter> outputTable = new Hashtable<String, FileWriter>();
    for(MovesetInfo defenderMovesetInfo : movesetInfoList){
      BasePokemon defenderBase = gameMaster.getPokemonTable().get(defenderMovesetInfo.getPokemonId());
      Move defenderQuick = gameMaster.getMoveTable().get(defenderMovesetInfo.getQuickMoveId());
      Move defenderCharge = gameMaster.getMoveTable().get(defenderMovesetInfo.getChargeMoveId());
      //String fileName = defenderBase.getName() + "." + defenderQuick.getName() + "." + defenderCharge.getName() + ".csv";
      //FileWriter writer = new FileWriter(outputFolder + fileName);
      FileWriter writer = outputTable.get(defenderBase.getName());
      if(writer == null){
        writer = new FileWriter(outputFolder + defenderBase.getName() + ".csv");
        outputTable.put(defenderBase.getName(), writer);
        writeHeader(writer, simulator instanceof MonteCarloFightSimulator);
      }
      System.out.println("Calculating: " + defenderBase.getName() + "." + defenderQuick.getName() + "." + defenderCharge.getName());
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
                result = simulator.calculateAttackDPS(tryAgainAttacker, defender, strategies[i], AttackStrategyType.DEFENSE_RANDOM);
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
              writeFightResultDetailed(writer, result);
            }
            writer.flush();
          }
        }
        currentDefLevel += levelIncrement;
      }
    }
    Set<String> outputKeySet = outputTable.keySet();
    for(String pokemonName : outputKeySet){
      FileWriter writer = outputTable.get(pokemonName);
      writer.close();
    }
  }
  

  private static void writeHeader(FileWriter writer, boolean isMonteCarlo) throws Exception {
    writer.write(PrintUtil.csvThis(new String[]{
        "Defender",
        "DefQuick",
        "DefCharge",
        "DefCP",
        "DefLevel",
        "Attacker",
        "AttQuick",
        "AttCharge",
        "AttCP",
        "AttLevel",
        "CombatTime",
        "AttHpLeft",
        "AttTotHp",
        "DefHpLeft",
        "DefTotHp",
        "AttPower%",
        "AttStrat"
    }));
    if(isMonteCarlo){
      writer.write(",");
      writer.write(PrintUtil.csvThis(new String[]{
        "Win%",
        "Timeout%"
      }));
    }
    if(OUTPUT_RANKING){
      writer.write(",");
      writer.write(PrintUtil.csvThis(new String[]{
        "PrestigeGained",
        "AttDPS",
        "AttHpLost",
        "AttHpLeft%",
        "DefHpLeft%",
        "TimePower%",
        "TotalPower%"
      }));
    }
    writer.write("\r\n");
  }
  
  private static void writeFightResultDetailed(FileWriter writer, FightResult result) throws Exception {
    CombatantResult attackerResult = result.getCombatant(0);
    Pokemon attacker = attackerResult.getPokemon();
    CombatantResult defenderResult = result.getCombatant(1);
    Pokemon defender = defenderResult.getPokemon();
    writer.write(PrintUtil.csvThis(new String[]{
        defender.getBasePokemon().getName(),
        defender.getQuickMove().getName(),
        defender.getChargeMove().getName(),
        "" + defender.getCp(),
        "" + ((double)defender.getLevel())/10,
        attacker.getBasePokemon().getName(),
        attacker.getQuickMove().getName(),
        attacker.getChargeMove().getName(),
        "" + attacker.getCp(),
        "" + ((double)attacker.getLevel())/10,
        "" + result.getTotalCombatTime(),
        "" + attackerResult.getEndHp(),
        "" + attackerResult.getStartHp(),
        "" + defenderResult.getEndHp(),
        "" + defenderResult.getStartHp(),
        (result.getPower()*100) + "",
        attackerResult.getStrategy().name()
    }));
    if(result instanceof MonteCarloFightResult){
      writer.write(",");
      writer.write(PrintUtil.csvThis(new String[]{
        "" + (((MonteCarloFightResult)result).getWinRatio()*100),
        "" + (((MonteCarloFightResult)result).getTimeoutRatio()*100)
      }));
    }
    if(OUTPUT_RANKING){
      writer.write(",");
      double timeToKill = ((double)defenderResult.getStartHp())/attackerResult.getDps();
      double timePower = ((double)Formulas.MAX_COMBAT_TIME_MS)/(1000*timeToKill);
      writer.write(PrintUtil.csvThis(new String[]{
          "" + result.getPrestige(),
          "" + attackerResult.getDps(),
          "" + (attackerResult.getStartHp() - (attackerResult.getEndHp()>0?attackerResult.getEndHp():0)),
          "" + (attackerResult.getEndHp()<1?"0":((double)attackerResult.getEndHp()*100)/attackerResult.getStartHp()),
          "" + (defenderResult.getEndHp()<1?"0":((double)defenderResult.getEndHp()*100)/defenderResult.getStartHp()),
          "" + timePower*100,
          "" + Math.sqrt((timePower*result.getPower()))*100
      }));
    }
    writer.write("\r\n");
  }
}
