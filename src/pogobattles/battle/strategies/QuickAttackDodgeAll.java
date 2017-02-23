package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.gamemaster.Move;

public class QuickAttackDodgeAll implements AttackStrategy {
    private int extraDelay;
    public static final int CAST_TIME = 0;
    public static final int DODGE_WINDOW = 700;

    public AttackStrategyType getType() {
        return AttackStrategyType.DODGE_ALL3;
    }

    public QuickAttackDodgeAll(int extraDelay) {
        this.extraDelay = extraDelay;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // dodge special if we can
        if (defenderState.getNextMove() != null && defenderState.getTimeToNextDamage() > 0
                && !defenderState.isDodged()) {
            if (defenderState.getTimeToNextDamage() < DODGE_WINDOW + extraDelay) {
                return new PokemonAttack(Move.DODGE_MOVE, extraDelay);
            } else if (defenderState.getTimeToNextDamage() > attackerState.getPokemon().getQuickMove().getDuration() + extraDelay) {
                // we can sneak in a normal attack
                return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
            } else {
                // dodge perfect
                return new PokemonAttack(Move.DODGE_MOVE,
                        Math.max(0, defenderState.getTimeToNextDamage() - DODGE_WINDOW));
            }
        }
        return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);

    }

    public int getDelay() {
        return getDelay();
    }

}
