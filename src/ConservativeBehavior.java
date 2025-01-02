import java.util.Random;

public class ConservativeBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.2 * (1.0 + (marketShare / 100));
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
        return funds >= 2000 && currentGames < 2 && new Random().nextDouble() < 0.2;
    }

    @Override
    public int getTargetEmployeeCount() {
        return (int) (new Random().nextInt(4) + 1);
    }

    @Override
    public double getMaxSalaryPercentage() {
        return (double) (new Random().nextDouble(0.15) + 0.05);
    }

    @Override
    public String getBehaviorName() {
        return "Conservative";
    }
}