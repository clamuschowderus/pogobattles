package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;

public class QuickAttackOnly implements AttackStrategy {
    private int extraDelay;

    public AttackStrategyType getType() {
        return AttackStrategyType.QUICK_ATTACK_ONLY;
    }

    public QuickAttackOnly(int extraDelay) {
        this.extraDelay = extraDelay;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
    }

    public int getDelay() {
        return getDelay();
    }

}
