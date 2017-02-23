package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class DodgeAll2 implements AttackStrategy {
    private int extraDelay;
    private boolean dodgedSpecial = false;
    public static final int CAST_TIME = 0;

    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_ALL2;
    }

    public DodgeAll2(int extraDelay) {
        this.extraDelay = extraDelay;
        dodgedSpecial = false;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() < Formulas.DODGE_WINDOW + extraDelay) {
                dodgedSpecial = defenderState.isNextMoveSpecial();
                return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (defenderState.getTimeToNextDamage() > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                dodgedSpecial = false;
                // we can sneak in a normal attack
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
                dodgedSpecial = defenderState.isNextMoveSpecial();
                // dodge perfect
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta() && dodgedSpecial) {
            // use special attack after dodge
            dodgedSpecial = false;
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
        } else {
            dodgedSpecial = false;
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

}
