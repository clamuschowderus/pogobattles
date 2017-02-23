package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;
import pogobattles.gamemaster.Move;

public class NoAttack implements AttackStrategy {
    int extraDelay;

    public AttackStrategyType getType() {
        return AttackStrategyType.NONE;
    }

    public NoAttack(int extraDelay) {
        this.extraDelay = extraDelay;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
                // dodge perfect
            return new PokemonAttack(Move.DODGE_MOVE,
                    Math.max(0, defenderState.getTimeToNextDamage() - Formulas.DODGE_WINDOW));
        }        
        return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
    }

    public int getDelay() {
        return getDelay();
    }

}
