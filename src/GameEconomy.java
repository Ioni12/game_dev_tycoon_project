import java.util.List;
import java.util.Random;

public class GameEconomy {
    // Market constants
    public static final double BASE_MARKET_SIZE = 5000000; // Base market size in dollars
    public static final double MARKET_GROWTH_RATE = 0.03; // 5% quarterly growth
    public static final double MAX_MARKET_SHARE_CHANGE = 0.015; // 2% max change per quarter

    // Game development constants
    public static final double MARKETING_MULTIPLIER = 0.25;
    public static final double DEVELOPMENT_COST_MULTIPLIER = 0.2;
    public static final double QUALITY_IMPACT = 0.4;

    private static final double MIN_QUARTERLY_REVENUE = 30000;
    private static final double MARKET_SHARE_REVENUE_MULTIPLIER = 1000;

    public static double calculateGameRevenue(Game game, double marketShare, double totalMarketSize) {
        // Guarantee minimum return on investment
        double baseRevenue = game.getBudget() * 3.5;

        // Improved quality impact
        double qualityBonus = Math.pow(1 + (game.getQuality() / 100.0), QUALITY_IMPACT);

        // Better market share scaling
        double marketBonus = Math.pow(marketShare / 100.0, 0.7);

        // Genre-specific multiplier
        double genreMultiplier = game.getGenre().getQualityMultiplier();

        // Calculate total revenue with better scaling
        double totalRevenue = baseRevenue * qualityBonus * marketBonus * genreMultiplier;

        // Ensure minimum profitability
        return Math.max(baseRevenue * qualityBonus * marketBonus * genreMultiplier,
                game.getBudget() * 1.5);
    }

    public static double calculateDevelopmentCosts(Game game, List<Employee> developers) {
        // Reduced base cost
        double baseCost = game.getBudget() * DEVELOPMENT_COST_MULTIPLIER;

        // Reduced employee costs
        double employeeCosts = developers.stream()
                .mapToDouble(Employee::getSalary)
                .sum() * (game.getGenre().getMonthsToComplete() / 36.0); // Divided by 24 instead of 12

        double complexityMultiplier = game.getGenre().getQualityMultiplier();
        return (baseCost + employeeCosts) * complexityMultiplier;
    }

    public static double calculateBaseQuarterlyRevenue(double marketShare) {
        // Guaranteed minimum revenue based on market share
        double baseRevenue = MIN_QUARTERLY_REVENUE + (marketShare * MARKET_SHARE_REVENUE_MULTIPLIER);
        return baseRevenue * (0.8 + new Random().nextDouble() * 0.4); // Add some randomness
    }

    public static double calculateMarketShareChange( double currentShare, double companyPerformance, double industryAveragePerformance) {
        double performanceRatio = companyPerformance / industryAveragePerformance;
        double baseChange = (performanceRatio - 1.0) * MAX_MARKET_SHARE_CHANGE;

        // Limit market share changes
        return Math.max(-MAX_MARKET_SHARE_CHANGE,
                Math.min(MAX_MARKET_SHARE_CHANGE, baseChange));
    }

    public static double calculateEmployeeSalary(int skillLevel, int productivity, double companyRevenue) {
        double baseSalary = 200 + (skillLevel * 300); // Reduced base and skill multiplier
        double productivityMultiplier = 1.0 + (productivity - 50) / 200.0; // Reduced productivity impact
        double companyBonus = Math.log10(companyRevenue / 10000) * 25; // Adjusted bonus scaling

        // Add salary cap based on company revenue
        double salaryCap = companyRevenue * 0.1;
        return Math.min((baseSalary * productivityMultiplier) + companyBonus, salaryCap);
    }
}