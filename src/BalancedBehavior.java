import java.util.Random;

public class BalancedBehavior implements CompanyBehavior {
    @Override
    public double calculateGameBudget(double funds, double marketShare) {
        return funds * 0.2 * (1.0 + (marketShare / 100));
    }

    @Override
    public Game.Genre selectGenre(double funds, double marketShare) {
        // Varied genre selection
        return Game.Genre.values()[new Random().nextInt(Game.Genre.values().length)];
    }

    @Override
    public boolean shouldStartNewGame(double funds, int currentGames) {
        return funds >= 1500 && currentGames < 2 && new Random().nextDouble() < 0.3;
    }

    @Override
    public int getTargetEmployeeCount() {
        return (int) (new Random().nextInt(6) + 1);
    }

    @Override
    public double getMaxSalaryPercentage() {
        return (double) (new Random().nextDouble(0.2) + 0.1);
    }

    @Override
    public String getBehaviorName() {
        return "Balanced";
    }
}