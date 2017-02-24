# pogobattles
pokebattler simulations generator. 

First of all: Many, many thanks to reddit user celandro. I downloaded his code (https://github.com/celandro/pokebattler-fight) and incorporated into my own GAME_MASTER parser (with a fair amount of data model changes to accomodate my parser).

Second: Feel free to download and modify/play with the code. I just politely ask you to give credit to celandro if you do so for developing the core of pokebattler.

Two main methods to run: pogobattles.Main.main() for simulations or pogobattles.PrintMain.main() to print out GAME_MASTER information.

Point BASE_FOLDER value to the resource folder location in both pogobattles.Main and pogobattles.PrintMain

Play with the parameters in pogobattles.Main to get different results. Should be pretty stright forward. If you need more explanation read-on.

pogobattles.ranking.RankingsCalculator has a few methods that take different parameters to drive the results. I'll list them down here:

-   public static void calculateAllPrestige(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int defLevel, boolean optimal);
    - outputFolder in String format;
    - simulator is the pokebattler engine instance (you can refer to Main.main to see how it's instantiated)
    - gameMaster contains all relevant GAME MASTER info (refer to Main.main to see how it's built, it uses ParserNew.parse())
    - movesets contains all movesets to be used in the simulation. This info is loaded from the allMovesetsForSim.csv file which contain
        PokemonDexNum, QuickMoveId, ChargeMoveId for all possible movesets in the game.
    - defenders is a list of defenders names. Look for examples in Main.main().
    - defLevel - defender pokemon's level.
    - optimal - flag to tell the simulation to look for optimal prestigers. What it does is that the simulation will try first with 1/2 CP. If it results in a no-win scenario, the simulation will go up half a level and try again, until it either reaches MAX level (40) or finds a winning pokemon.
    - CONSIDERATIONS: This uses perfect IV pokemon for both attack and defense and there's no limit to attacker level.
    
-   public static void calculateAllPrestige(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int maxAttLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel, boolean optimal);
    - THIS METHOD adds the flexibility of specifying IVs and also a maxAttackerLevel;

calculateAll methods listed below will pitch all attackers in the movesets List against the defenders in the defenders List at a specified attacker and defender level. Fields are similar to described above for the calculateAllPrestige methods.

- public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int attLevel, int defLevel);
- public static void calculateAll(String outputFolder, AttackSimulator simulator, GameMaster gameMaster, MovesetTable movesets, List<String> defenders, int ivAtt, int ivDef, int ivSta, int attLevel, int defIvAtt, int defIvDef, int defIvSta, int defLevel);

Sorry for the overall horrible quality of the code. I vomited this code in record time. I'll try to come back and make it a bit more user friendly. That is if celandro doesn't decide to just incorporate this sort of functionality in his own project (I hope he does), then there'll be no more need for any of this here.
