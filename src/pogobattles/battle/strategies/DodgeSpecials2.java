package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class DodgeSpecials2 implements AttackStrategy {
    private int extraDelay;
    private boolean dodgedSpecial;
    public static final int CAST_TIME = 0;

    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_SPECIALS2;
    }

    public DodgeSpecials2(int extraDelay) {
        this.extraDelay = extraDelay;
        dodgedSpecial = false;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.isNextMoveSpecial()
                && defenderState.getTimeToNextDamage() > 0 && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() <= Formulas.DODGE_WINDOW + extraDelay) {
                dodgedSpecial = true;
                return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (defenderState.getTimeToNextDamage() > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
                // dodge perfect
                dodgedSpecial = true;
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
            }
        }
        if (attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta() && dodgedSpecial) {
            dodgedSpecial = false;
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay + CAST_TIME);
        } else {
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
        }

    }

    public int getDelay() {
        return getDelay();
    }

}
