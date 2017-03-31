package pogobattles.battle.strategies;

import pogobattles.battle.simulator.CombatantState;
import pogobattles.gamemaster.Move;

public interface AttackStrategy {
    public PokemonAttack nextAttack(CombatantState attackerState, CombatantState defenderState);

    public AttackStrategyType getType();

    public static final int DODGE_COOLDOWN = 500;

    public static class PokemonAttack {
        private Move move;
        private int delay;

        public PokemonAttack(Move move, int delay) {
            //System.out.println(move.getName());
            this.move = move;
            this.delay = delay;
        }

        public Move getMove() {
            return move;
        }

        public int getDelay() {
            return delay;
        }

    }
}
