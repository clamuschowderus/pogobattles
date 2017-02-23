package pogobattles.gamemaster;

public class BattleSettings {
/*
 *                                                                                                      'dodge_damage_reduction_percent': 0.75,
                                                                                                     'dodge_duration_ms': 500,
                                                                                                     'enemy_attack_interval': 1.5,
                                                                                                     'energy_delta_per_health_lost': 0.5,
                                                                                                     'maximum_attackers_per_battle': 20,
                                                                                                     'maximum_energy': 100,
                                                                                                     'minimum_player_level': 5,
                                                                                                     'retarget_seconds': 0.5,
                                                                                                     'round_duration_seconds': 99.0,
                                                                                                     'same_type_attack_bonus_multiplier': 1.25,
                                                                                                     'swap_duration_ms': 1000},

 */
  private double dodgeDamageReduction;
  private int dodgeDuration;
  private double enemyAttackInterval;
  private double energyDeltaPerHealthLost;
  private int maximumEnergy;
  private double roundDuration;
  private double stabMultiplier;
  private int timeToChargeText = 700; //700 ms by default for the charge text to show on screen (according to gamepress study)
  private int dodgeWindow = 700; //700 ms by default time to dodge after the flash (according to gamepress study)
  
  public double getDodgeDamageReduction() {
    return dodgeDamageReduction;
  }
  public void setDodgeDamageReduction(double dodgeDamageReduction) {
    this.dodgeDamageReduction = dodgeDamageReduction;
  }
  public int getDodgeDuration() {
    return dodgeDuration;
  }
  public void setDodgeDuration(int dodgeDuration) {
    this.dodgeDuration = dodgeDuration;
  }
  public double getEnemyAttackInterval() {
    return enemyAttackInterval;
  }
  public void setEnemyAttackInterval(double enemyAttackInterval) {
    this.enemyAttackInterval = enemyAttackInterval;
  }
  public double getEnergyDeltaPerHealthLost() {
    return energyDeltaPerHealthLost;
  }
  public void setEnergyDeltaPerHealthLost(double energyDeltaPerHealthLost) {
    this.energyDeltaPerHealthLost = energyDeltaPerHealthLost;
  }
  public int getMaximumEnergy() {
    return maximumEnergy;
  }
  public void setMaximumEnergy(int maximumEnergy) {
    this.maximumEnergy = maximumEnergy;
  }
  public double getRoundDuration() {
    return roundDuration;
  }
  public void setRoundDuration(double roundDuration) {
    this.roundDuration = roundDuration;
  }
  public double getStabMultiplier() {
    return stabMultiplier;
  }
  public void setStabMultiplier(double stabMultiplier) {
    this.stabMultiplier = stabMultiplier;
  }
  public int getTimeToChargeText() {
    return timeToChargeText;
  }
  public void setTimeToChargeText(int timeToChargeText) {
    this.timeToChargeText = timeToChargeText;
  }
  public int getDodgeWindow() {
    return dodgeWindow;
  }
  public void setDodgeWindow(int dodgeWindow) {
    this.dodgeWindow = dodgeWindow;
  }
}
