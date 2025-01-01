public class InnovativeBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.5 * (1.0 + (marketShare / 100));
    }

    @Override
    public Game.Genre selectGenre(double funds, double marketShare) {
        // Tends to avoid common genres
        double random = Math.random();
        if (random < 0.4) {
            return Game.Genre.RPG;
        } else if (random < 0.7) {
            return Game.Genre.STRATEGY;
        }
        return Game.Genre.SIMULATION;
    }

    @Override
    public boolean shouldStartNewGame(double funds, int currentGames) {
        return funds >= 1200 && currentGames < 2;
    }

    @Override
    public int getTargetEmployeeCount() {
        return 7;
    }

    @Override
    public double getMaxSalaryPercentage() {
        return 0.25;
    }

    @Override
    public String getBehaviorName() {
        return "Innovative";
    }
}