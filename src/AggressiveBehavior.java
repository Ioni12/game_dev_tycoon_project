import java.util.Random;

public class AggressiveBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.05 * (1.0 + (marketShare / 150)); // Reduced from 12%
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
        return funds >= 15000 && currentGames < 3 && new Random().nextDouble() < 0.4;
    }

    @Override
    public int getTargetEmployeeCount() {
        return (int) (new Random().nextInt(4) + 1);
    }

    @Override
    public double getMaxSalaryPercentage() {
        return (double) (new Random().nextDouble(0.2) + 0.08); // Reduced salary cap
    }

    @Override
    public String getBehaviorName() {
        return "Aggressive";
    }
}

