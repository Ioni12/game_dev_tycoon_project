import java.util.List;

public class GameEconomy {
    // Market constants
    public static final double BASE_MARKET_SIZE = 1000000; // Base market size in dollars
    public static final double MARKET_GROWTH_RATE = 0.05; // 5% quarterly growth
    public static final double MAX_MARKET_SHARE_CHANGE = 0.02; // 2% max change per quarter

    // Game development constants
    public static final double MARKETING_MULTIPLIER = 0.3;
    public static final double DEVELOPMENT_COST_MULTIPLIER = 1.2;
    public static final double QUALITY_IMPACT = 0.4;

    public static double calculateGameRevenue(Game game, double marketShare, double totalMarketSize) {
        // Base revenue calculation
        double qualityFactor = Math.pow(1 + (game.getQuality() / 100.0), QUALITY_IMPACT);
        double marketFactor = Math.sqrt(marketShare / 100.0);
        double genreMultiplier = game.getGenre().getQualityMultiplier();

        // Calculate potential revenue
        double potentialRevenue = game.getBudget() * qualityFactor * marketFactor * genreMultiplier;

        // Apply market size constraints
        double marketCapacity = totalMarketSize * (marketShare / 100.0);
        return Math.min(potentialRevenue, marketCapacity);
    }

    public static double calculateDevelopmentCosts(Game game, List<Employee> developers) {
        // Base development costs
        double baseCost = game.getBudget() * DEVELOPMENT_COST_MULTIPLIER;

        // Employee costs
        double employeeCosts = developers.stream()
                .mapToDouble(Employee::getSalary)
                .sum() * (game.getGenre().getMonthsToComplete() / 12.0);

        // Additional costs based on genre complexity
        double complexityMultiplier = game.getGenre().getQualityMultiplier();

        return (baseCost + employeeCosts) * complexityMultiplier;
    }

    public static double calculateMarketShareChange(
            double currentShare,
            double companyPerformance,
            double industryAveragePerformance) {
        double performanceRatio = companyPerformance / industryAveragePerformance;
        double baseChange = (performanceRatio - 1.0) * MAX_MARKET_SHARE_CHANGE;

        // Limit market share changes
        return Math.max(-MAX_MARKET_SHARE_CHANGE,
                Math.min(MAX_MARKET_SHARE_CHANGE, baseChange));
    }

    public static double calculateEmployeeSalary(
            int skillLevel,
            int productivity,
            double companyRevenue) {
        // Base salary based on skill
        double baseSalary = 3000 + (skillLevel * 750);

        // Productivity bonus
        double productivityMultiplier = 1.0 + (productivity - 50) / 100.0;

        // Company performance bonus
        double companyBonus = Math.log10(companyRevenue / 10000) * 100;

        return (baseSalary * productivityMultiplier) + companyBonus;
    }
}