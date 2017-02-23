package pogobattles.battle.data;

import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;

public class PokemonDataCreator {
    GameMaster gameMaster;

    public PokemonDataCreator() {
        
    }
    public PokemonDataCreator(GameMaster gameMaster) {
      this.gameMaster = gameMaster;
    }
    
    public void initCPLookupMap() {
    }

    public Pokemon createMaxStatPokemon(BasePokemon basePokemon, Integer level, Move move1, Move move2) {
        return createPokemon(basePokemon, level, gameMaster.getMaxIvStat(), gameMaster.getMaxIvStat(),
            gameMaster.getMaxIvStat(), move1, move2);
    }

    public Pokemon createPokemon(BasePokemon basePokemon, Integer level, int ivAttack, int ivDefense,
            int ivStamina, Move move1, Move move2) {
        double cpm = gameMaster.getCpMultiplierTable().get(level);
        BasePokemon p = basePokemon;
        Pokemon retval = new Pokemon();
        retval.setBasePokemon(basePokemon);
        retval.setIvAttack(ivAttack);
        retval.setIvDefense(ivDefense);
        retval.setIvStamina(ivStamina);
        retval.setLevel(level);
        retval.setCpMultiplier(cpm);
        retval.setQuickMove(move1);
        retval.setChargeMove(move2);
        return retval;
    }

    public Pokemon createPokemon(BasePokemon basePokemon, int maxCp, Move move1, Move move2) {
        Integer maxLevel = (gameMaster.getCpMultiplierTable().size()+1)*5;
        return createPokemon(basePokemon, maxCp, maxLevel, gameMaster.getMaxIvStat(), gameMaster.getMaxIvStat(), gameMaster.getMaxIvStat(), move1, move2);
    }
    
    public Pokemon createPokemon(BasePokemon basePokemon, int maxCp, Integer maxLevel, int ivAttack, int ivDefense, int ivStamina, Move move1, Move move2){
        BasePokemon p = basePokemon;
        Integer level = findMaxLevel(basePokemon, maxCp, maxLevel, ivAttack, ivDefense, ivStamina);
        return createPokemon(basePokemon, level, ivAttack, ivDefense, ivStamina, move1, move2);
    }
    
    public Integer findMaxLevel(BasePokemon basePokemon, int maxCp, Integer maxLevel, int ivAttack, int ivDefense, int ivStamina){
        Integer retVal = maxLevel;
        int helperAtt = basePokemon.getBaseAttack() + ivAttack;
        int helperDef = basePokemon.getBaseDefense() + ivDefense;
        int helperSta = basePokemon.getBaseStamina() + ivStamina;
        double maxCpM = Math.sqrt((10*maxCp)/(helperAtt*Math.sqrt(helperDef)*Math.sqrt(helperSta)));
        while(maxCpM < gameMaster.getCpMultiplierTable().get(retVal) && retVal > 10){
          retVal -=5;
        }
        //System.out.println("Pokemon: " + basePokemon.getName() + ". MaxCp: " +maxCp + ". Level: " + retVal);
        return retVal;
    }

    public Pokemon transform(Pokemon attacker, Pokemon defender) {
        Pokemon retVal = new Pokemon();
        retVal.setBasePokemon(defender.getBasePokemon());
        retVal.setCpMultiplier(attacker.getCpMultiplier());
        retVal.setIvAttack(attacker.getIvAttack());
        retVal.setIvDefense(attacker.getIvDefense());
        // initially we set to normal stamina so we can calculate the proper CP;
        retVal.setIvStamina(attacker.getIvStamina());
        retVal.getCp(); // forces Cp calculation.
        // Transform keeps original pokemon stamina, but we can't alter the BasePokemon instance, so we trick the IV.
        retVal.setIvStamina(attacker.getBasePokemon().getBaseStamina()-defender.getBasePokemon().getBaseStamina()+attacker.getIvStamina());
        retVal.setQuickMove(defender.getQuickMove());
        retVal.setChargeMove(defender.getChargeMove());
        retVal.setLevel(attacker.getLevel());
        return retVal;
    }

}
