import java.util.Random;

public class ConservativeBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.03 * (1.0 + (marketShare / 200)); // Reduced from 8%
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
        return funds >= 20000 && currentGames < 2 && new Random().nextDouble() < 0.15;
    }

    @Override
    public int getTargetEmployeeCount() {
        return (int) (new Random().nextInt(1) + 1);
    }

    @Override
    public double getMaxSalaryPercentage() {
        return (double) (new Random().nextDouble(0.12) + 0.05); // Lower salary risk
    }

    @Override
    public String getBehaviorName() {
        return "Conservative";
    }
}