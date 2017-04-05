package pogobattles.battle.strategies;

import java.util.Random;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class DodgeAllRandom implements AttackStrategy {
    private int extraDelay;
    private Random r = new Random();
    private double dodgeRandom;
    public static final int CAST_TIME = 0;

    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_ALL;
    }

    public DodgeAllRandom(int extraDelay) {
        this.extraDelay = extraDelay;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() <= Formulas.DODGE_WINDOW + extraDelay) {
                return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (defenderState.getTimeToNextDamage() > attackerState.getPokemon().getChargeMove().getDuration() + extraDelay + CAST_TIME
                    && attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
                // we can sneak in a special attack
                return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
            } else if (defenderState.getTimeToNextDamage() > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
                // dodge perfect
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
        } else {
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

}
