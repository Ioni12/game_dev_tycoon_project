public class AggressiveBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.6 * (1.0 + (marketShare / 100));
    }

    @Override
    public Game.Genre selectGenre(double funds, double marketShare) {
        // Prefers complex, expensive genres
        double random = Math.random();
        if (random < 0.6) {
            return Game.Genre.RPG;
        } else if (random < 0.8) {
            return Game.Genre.ACTION;
        }
        return Game.Genre.STRATEGY;
    }

    @Override
    public boolean shouldStartNewGame(double funds, int currentGames) {
        return funds >= 1000 && currentGames < 3;
    }

    @Override
    public int getTargetEmployeeCount() {
        return 8;
    }

    @Override
    public double getMaxSalaryPercentage() {
        return 0.3;
    }

    @Override
    public String getBehaviorName() {
        return "Aggressive";
    }
}

