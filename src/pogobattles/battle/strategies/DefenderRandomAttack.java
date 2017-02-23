package pogobattles.battle.strategies;

import java.util.Random;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;

public class DefenderRandomAttack implements AttackStrategy {
    private int extraDelay;
    private Random r = new Random();
    public static int SECOND_ATTACK_DELAY = 1000;
    public static int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;
    private int randomDelay;
    private AttackStrategyType type;
    private double specialRandom;

    public static final int RAND_MS_DELAY = 1000;
    public static final double RAND_CHANCE_SPECIAL = 0.5;
    public static final int DEFENDER_DELAY = 2000;

    public static final int LUCKY_RAND_MS_DELAY = 1;
    public static final double LUCKY_RAND_CHANCE_SPECIAL = 1.0;
    public static final int LUCKY_RAND_LUCKY_DELAY = 50;
    public static final int LUCKY_DEFENDER_DELAY = 2000;
    
    public AttackStrategyType getType() {
        return type;
    }

    public DefenderRandomAttack(int extraDelay, int randomDelay,
            AttackStrategyType type, double specialRandom) {
        this.extraDelay = extraDelay;
        this.randomDelay = randomDelay;
        this.type = type;
        this.specialRandom = specialRandom;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (r.nextDouble() < specialRandom && attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + r.nextInt(randomDelay));
        } else {
            switch (attackerState.getNumAttacks()) {
            case 0:
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), FIRST_ATTACK_TIME);
            case 1:
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), SECOND_ATTACK_DELAY - attackerState.getPokemon().getQuickMove().getDuration());
            case 2:
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(),
                        extraDelay + r.nextInt(randomDelay) - attackerState.getPokemon().getQuickMove().getDuration());
            default:
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay + r.nextInt(randomDelay));
            }
        }

    }

}
