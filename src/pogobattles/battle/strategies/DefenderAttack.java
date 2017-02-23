package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.battle.simulator.Formulas;

public class DefenderAttack implements AttackStrategy {
    private int extraDelay;
    private int nextSpecialMove = -1;
    public static final int SECOND_ATTACK_DELAY = 1000;
    public static final int FIRST_ATTACK_TIME = 1600 - Formulas.START_COMBAT_TIME;

    public static int DEFENDER_DELAY = 2000;
    
    public AttackStrategyType getType() {
        return AttackStrategyType.DEFENSE;
    }

    public DefenderAttack(int extraDelay) {
        this.extraDelay = extraDelay;
        nextSpecialMove = -1;
    }

    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState) {
        // Statistically speaking with a 50% chance of winning a coin flip, you
        // average out at at attack coming out 1 attack later
        if (nextSpecialMove == -1 && attackerState.getCurrentEnergy() >= -1 * attackerState.getPokemon().getChargeMove().getEnergyDelta()) {
            nextSpecialMove = attackerState.getNumAttacks() + 1;
        }
        if (nextSpecialMove == attackerState.getNumAttacks()) {
            nextSpecialMove = -1;
            return new PokemonAttack(attackerState.getPokemon().getChargeMove(), extraDelay);
        }
        switch (attackerState.getNumAttacks()) {
        case 0:
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), FIRST_ATTACK_TIME);
        case 1:
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), SECOND_ATTACK_DELAY - attackerState.getPokemon().getQuickMove().getDuration());
        case 2:
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay - attackerState.getPokemon().getQuickMove().getDuration());
        default:
            return new PokemonAttack(attackerState.getPokemon().getQuickMove(), extraDelay);
        }

    }

}
