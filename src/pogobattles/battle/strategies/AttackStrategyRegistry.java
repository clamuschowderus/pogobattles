package pogobattles.battle.strategies;

import java.util.HashMap;
import java.util.Map;

public class AttackStrategyRegistry {
    private Map<AttackStrategyType, AttackStrategy> strategies;
    
    public void init() {
        strategies = new HashMap<AttackStrategyType, AttackStrategy>();
        
    }

    public AttackStrategy create(AttackStrategyType name) {
      switch(name){
      case QUICK_ATTACK_ONLY:
        return new QuickAttackOnly(0);
      case NONE:
        return new NoAttack(AttackStrategy.DODGE_COOLDOWN);
      case DEFENSE:
        return new DefenderAttack(DefenderAttack.DEFENDER_DELAY);
      case CINEMATIC_ATTACK_WHEN_POSSIBLE:
        return new CinematicAttackWhenPossible(0);
      case DEFENSE_RANDOM:
        return new DefenderRandomAttack(DefenderRandomAttack.DEFENDER_DELAY - (DefenderRandomAttack.RAND_MS_DELAY / 2), DefenderRandomAttack.RAND_MS_DELAY, AttackStrategyType.DEFENSE_RANDOM,
            DefenderRandomAttack.RAND_CHANCE_SPECIAL);
      case DEFENSE_LUCKY:
        return new DefenderRandomAttack(DefenderRandomAttack.LUCKY_DEFENDER_DELAY - DefenderRandomAttack.LUCKY_RAND_LUCKY_DELAY, DefenderRandomAttack.LUCKY_RAND_MS_DELAY, AttackStrategyType.DEFENSE_LUCKY,
            DefenderRandomAttack.LUCKY_RAND_CHANCE_SPECIAL);
      case DODGE_SPECIALS:
        return new DodgeSpecials(0);
      case DODGE_SPECIALS2:
        return new DodgeSpecials2(0);
      case DODGE_SPECIALS3:
        return new QuickAttackDodgeSpecials(0);
      case DODGE_ALL:
        return new DodgeAll(0);
      case DODGE_ALL2:
        return new DodgeAll2(0);
      case DODGE_ALL3:
        return new QuickAttackDodgeAll(0);
      case DODGE_ALL_CAUTIOUS_HUMAN:
        return new DodgeAllHuman(0, AttackStrategyType.DODGE_ALL_CAUTIOUS_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_CAUTIOUS);
      case DODGE_ALL_REASONABLE_HUMAN:
        return new DodgeAllHuman(0, AttackStrategyType.DODGE_ALL_REASONABLE_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_REASONABLE);
      case DODGE_ALL_RISKY_HUMAN:
        return new DodgeAllHuman(0, AttackStrategyType.DODGE_ALL_RISKY_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_RISKY);
      case DODGE_ALL_RECKLESS_HUMAN:
        return new DodgeAllHuman(0, AttackStrategyType.DODGE_ALL_RECKLESS_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_RECKLESS);
      case DODGE_ALL_MAX_RISK_HUMAN:
          return new DodgeAllHuman(0, AttackStrategyType.DODGE_ALL_MAX_RISK_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_MAX_RISK);
      case DODGE_SPECIALS_HUMAN:
        return new DodgeAllHuman(0, AttackStrategyType.DODGE_SPECIALS_HUMAN, DodgeAllHuman.MIN_DEFENDER_DELAY_DODGE_SPECIALS);
      case DODGE_RANDOM_HUMAN:
        return new DodgeRandomHuman(0, AttackStrategyType.DODGE_RANDOM_HUMAN, DodgeRandomHuman.MIN_DEFENDER_DELAY_RANDOM);
      case DODGE_RANDOM_HUMAN_SPECIALS:
        return new DodgeRandomHuman(0, AttackStrategyType.DODGE_RANDOM_HUMAN_SPECIALS, DodgeRandomHuman.MIN_DEFENDER_DELAY_RANDOM_SPECIALS_ONLY);
      case DODGE_SPECIALS_NINJA:
    	return new DodgeAllHuman(0, AttackStrategyType.DODGE_SPECIALS_NINJA, DodgeAllHuman.MIN_DEFENDER_DELAY_DODGE_SPECIALS, true);
      default:
        throw new IllegalArgumentException("AttackStrategyNotFound");
      }
    }

}
