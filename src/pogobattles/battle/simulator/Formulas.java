package pogobattles.battle.simulator;

import java.util.Random;

import pogobattles.gamemaster.BasePokemon;
import pogobattles.gamemaster.GameMaster;
import pogobattles.gamemaster.Move;

public class Formulas {
    private static final int DODGE_MODIFIER = 4;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 40;
    public static final int MIN_INDIVDIUAL_STAT = 0;
    public static final int MAX_INDIVDIUAL_STAT = 15;
    public static final int MAX_COMBAT_TIME_MS = 100000;
    public static final int START_COMBAT_TIME = 700;
    public static final int DODGE_WINDOW = 700;
    public static final int MAX_DEFENDER_ENERGY_POOL = 100;
    public static final int MAX_ATTACKER_ENERGY_POOL = 100;
    //private static final Random r = new Random();

    public static int getCurrentHP(int baseStam, int indStam, double cpm) {
        return getCurrentHP(baseStam + indStam, cpm);
    }
    public static int getCurrentHP(int stam, double cpm) {
        return (int) (stam * cpm);
    }

    public static double getCurrentAttack(int baseAttack, int indAttack, double cpm) {
        return (baseAttack + indAttack) * cpm;
    }

    public static double getCurrentDefense(int baseDefense, int indDefense, double cpm) {
        return (baseDefense + indDefense) * cpm;
    }

    public static int getDefenderHp(int baseStam, int indStam, double cpm) {
        return 2 * getCurrentHP(baseStam, indStam, cpm);
    }
    public static int getDefenderHp(int stam, double cpm) {
        return 2 * getCurrentHP(stam, cpm);
    }

    public static double calculateModifier(GameMaster gameMaster, Move move, BasePokemon attacker, BasePokemon defender) {
        double modifier = 1.0;
		    if (move.getType() == attacker.getType() || move.getType() == attacker.getType2()) {
            modifier *= 1.25; // stab
        }
        modifier *= gameMaster.getTypeAdvantage()[move.getType()-1][defender.getType()-1];
        if(defender.getType2() != 0){
          modifier *= gameMaster.getTypeAdvantage()[move.getType()-1][defender.getType2()-1];
        }

        return modifier;
    }


    public static CombatResult getCombatResult(GameMaster gameMaster, double attack, double defense, Move move, BasePokemon attacker,
            BasePokemon defender, boolean isDodge) {
        int damage = damageOfMove(gameMaster, attack, defense, move, attacker, defender);
        return getCombatResult(damage, move, isDodge);
    }
    public static int getDamageOfMove(GameMaster gameMaster, double attack, double defense, Move move, BasePokemon attacker,
            BasePokemon defender) {
        return damageOfMove(gameMaster, attack, defense, move, attacker, defender);
    }
	public static CombatResult getCombatResult(final int damage, Move move, boolean isDodge) {
		final int dodgeDamage;
        if (isDodge) {
            // divide by 4 round down but with min of 1
            dodgeDamage = Math.max(1, damage/DODGE_MODIFIER);
        } else {
            dodgeDamage = damage;
        }
        CombatResult result = new CombatResult();
        result.setCombatTime(move.getDuration());
        result.setDamage(dodgeDamage);
        result.setDamageTime(move.getDamageWindowEnd());
        result.setAttackMove(move);
        result.setDodgePercent((float)damage/dodgeDamage);
        result.setCriticalHit(false);
        return result;
	}

    public static int damageOfMove(GameMaster gameMaster, double attack, double defense, Move move, BasePokemon attacker, BasePokemon defender) {
        if (move.getMoveId() == Move.DODGE_ID) {
            return 0;
        }
        double modifier = calculateModifier(gameMaster, move, attacker, defender);
        //System.out.println(move.getName()+": "+modifier);

        // critical hits are not implemented
        double critMultiplier = 1.0;
        // misses are not implemented
        double missMultiplier = 1.0;

        // int damage = Math.max(1, (int) ((45.0 / 100.0 * attack / defense *
        // move.getPower() + 0.8)
        // * (wasCrit?1.5:1.0) * move.getAccuracyChance() * modifier));
        // rounds up if possible
        int damage = (int) (0.5 * attack / defense * move.getDamage() * critMultiplier * missMultiplier * modifier) + 1;
        return damage;
    }

    public static int defensePrestigeGain(double attack, double... defenses) {
        int prestigeGain = 0;
        for (double defense : defenses) {
            if (attack < defense) {
                prestigeGain += Math.min(1000, (int) (500.0 * defense / attack));
            } else {
                prestigeGain += Math.max(100, (int) (310.0 * defense / attack - 55)) ;
            }
        }
        return prestigeGain;
    }

    public static int energyGain(int damage) {
        //TODO: chage to use game master's energy gain per health lost modifier
        return (damage + 1) / 2;
    }
}
