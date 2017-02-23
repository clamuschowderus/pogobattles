package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;

public class CinematicAttackWhenPossible implements AttackStrategy {
    private int extraDelay;
    public static final int CAST_TIME = 0;

    public AttackStrategyType getType() {
        return AttackStrategyType.CINEMATIC_ATTACK_WHEN_POSSIBLE;
    }

    public CinematicAttackWhenPossible(int extraDelay) {
        this.extraDelay = extraDelay;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
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
