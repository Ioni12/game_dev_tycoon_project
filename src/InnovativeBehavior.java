import java.util.Random;

public class InnovativeBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.2 * (1.0 + (marketShare / 100));
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
        return funds >= 1200 && currentGames < 2 && new Random().nextDouble() < 0.4;
    }

    @Override
    public int getTargetEmployeeCount() {
        return (int) (new Random().nextInt(7) + 1);
    }

    @Override
    public double getMaxSalaryPercentage() {
        return (double) (new Random().nextDouble(0.25) + 0.05);
    }

    @Override
    public String getBehaviorName() {
        return "Innovative";
    }
}