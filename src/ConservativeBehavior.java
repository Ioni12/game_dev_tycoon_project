public class ConservativeBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.3 * (1.0 + (marketShare / 100));
    }

    @Override
    public Game.Genre selectGenre(double funds, double marketShare) {
        // Prefers safer, cheaper genres
        double random = Math.random();
        if (random < 0.5) {
            return Game.Genre.CASUAL;
        } else if (random < 0.8) {
            return Game.Genre.SIMULATION;
        }
        return Game.Genre.STRATEGY;
    }

    @Override
    public boolean shouldStartNewGame(double funds, int currentGames) {
        return funds >= 2000 && currentGames < 2;
    }

    @Override
    public int getTargetEmployeeCount() {
        return 4;
    }

    @Override
    public double getMaxSalaryPercentage() {
        return 0.15;
    }

    @Override
    public String getBehaviorName() {
        return "Conservative";
    }
}