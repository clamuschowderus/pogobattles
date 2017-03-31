package pogobattles.battle.simulator;

import pogobattles.battle.data.Pokemon;
import pogobattles.battle.strategies.AttackStrategy.PokemonAttack;
import pogobattles.battle.strategies.AttackStrategyType;
import pogobattles.gamemaster.Move;

public class CombatantState {
    private double attack;
    private double defense;
    private final int startHp;
    private final long id;
    private final int cp;
    private Pokemon pokemon;
    private final boolean defender;
    private int timeSinceLastMove;
    private int currentHp;
    private int currentEnergy;
    private int combatTime;
    private int damageDealt;
    private int numAttacks;
    private PokemonAttack nextAttack;
    private Move nextMove;
    private boolean dodged = false;
    private boolean damageAlreadyOccurred = false;

    public boolean isNextMoveSpecial() {
        return !(getNextMove().getEnergyDelta() > 0);
    }

    public boolean isDodged() {
        return dodged;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getTimeSinceLastMove() {
        return timeSinceLastMove;
    }

    public double getAttack() {
        return attack;
    }

    public double getDefense() {
        return defense;
    }

    public int getStartHp() {
        return startHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getCombatTime() {
        return combatTime;
    }

    public int getActualCombatTime() {
        return combatTime + timeSinceLastMove;
    }

    public int getNumAttacks() {
        return numAttacks;
    }

    public long getId() {
        return id;
    }

    public PokemonAttack getNextAttack() {
        return nextAttack;
    }

    public Move getNextMove() {
        return nextMove;
    }

    public CombatantState(Pokemon p, boolean defender) {
        this.id = p.getBasePokemon().getPokemonId();
        this.pokemon = p;
        this.cp = p.getCp();
        this.attack = Formulas.getCurrentAttack(p.getBasePokemon().getBaseAttack(), p.getIvAttack(),
                p.getCpMultiplier());
        this.defense = Formulas.getCurrentDefense(p.getBasePokemon().getBaseDefense(), p.getIvDefense(),
                p.getCpMultiplier());
        this.defender = defender;
        this.startHp = this.currentHp = defender
                ? Formulas.getDefenderHp(p.getBasePokemon().getBaseStamina(), p.getIvStamina(), p.getCpMultiplier())
                : Formulas.getCurrentHP(p.getBasePokemon().getBaseStamina(), p.getIvStamina(), p.getCpMultiplier());
        // TODO: Fix this when we want to support multiple fights in a row
        this.combatTime = 0;
        this.timeSinceLastMove = 0;
        this.nextAttack = null;
        this.nextMove = null;
    }

    boolean isAlive() {
        return currentHp > 0;
    }

    public int getTimeToNextAttack() {
        int delay = nextAttack.getDelay();
        int duration = nextMove.getDuration();
        return delay + duration - getTimeSinceLastMove();
    }

    public int getTimeToNextDamage() {
        if (damageAlreadyOccurred) {
            return Integer.MAX_VALUE;
        } else {
            // some moves on defense this will return negative on move 2 which causes bad things
            return Math.max(0, nextAttack.getDelay() + nextMove.getDamageWindowStart() - getTimeSinceLastMove());
        }
    }

    int applyDefense(CombatResult r, int time) {
        int energyGain = Formulas.energyGain(r.getDamage());
        currentEnergy = Math.max(0, Math.min(defender ? 200 : 100, currentEnergy + energyGain));
        currentHp -= r.getDamage();
        timeSinceLastMove += time;
        combatTime += r.getCombatTime();
        if (r.getAttackMove().getMoveId() == Move.DODGE_ID
                && getTimeToNextDamage() <= Formulas.DODGE_WINDOW && getTimeToNextDamage() >= 0 ) {
            dodged = true;
        }
        return energyGain;
    }

    void applyAttack(CombatResult r, int time) {
        numAttacks++;
        //if(numAttacks < 3){
        //  System.out.println("Applying " + getPokemon().getBasePokemon().getName() + " attack! " + getActualCombatTime());
        //}

        timeSinceLastMove += time;
        damageAlreadyOccurred = true;
        combatTime += time;
        damageDealt += r.getDamage();

    }
    int resetAttack(int time) {
        // energy gets subtracted at the very end, no energy gain
        int energyGain = nextMove.getEnergyDelta();
        currentEnergy = Math.max(0, Math.min(defender ? 200 : 100, currentEnergy + energyGain));
        // reset things that happen in between attacks
        combatTime += time;
        timeSinceLastMove = 0; // -1 * delay;
        nextAttack = null;
        nextMove = null;
        dodged = false;
        damageAlreadyOccurred = false;
        return energyGain;
    }
    void moveTime(int time) {
        combatTime += time;
        timeSinceLastMove += time;
    }

    public CombatantResult toResult(AttackStrategyType strategy, int actualCombatTime) {
        CombatantResult retval = new CombatantResult();
        retval.setStrategy(strategy);
        retval.setDamageDealt(getDamageDealt());
        retval.setCp(cp);
        retval.setCombatTime(actualCombatTime);
        retval.setDps(1000.0f * (getDamageDealt()) / actualCombatTime);
        retval.setEnergy(getCurrentEnergy());
        retval.setStartHp(getStartHp());
        retval.setEndHp(getCurrentHp());
        retval.setPokemon(pokemon);
        return retval;
    }

    public void setNextAttack(PokemonAttack nextAttack, Move nextMove) {
        this.nextAttack = nextAttack;
        this.nextMove = nextMove;
    }

}
