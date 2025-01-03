import java.util.List;

public class Game {
    private String title;
    private Genre genre;
    private double budget;
    private int developmentProgress;
    private int quality;
    private boolean completed;
    private static final int PROGRESS_MULTIPLIER = 50;
    private Company company; // Add this field
    private Market market;

    public enum Genre {
        ACTION(6, 1.15),     // Reduced from 12 months
        CASUAL(3, 0.85),     // Reduced from 6 months
        RPG(9, 1.35),        // Reduced from 18 months
        STRATEGY(5, 1.05),   // Reduced from 10 months
        SIMULATION(4, 0.95);

        private final int monthsToComplete;
        private final double qualityMultiplier;

        Genre(int months, double multiplier) {
            this.monthsToComplete = months;
            this.qualityMultiplier = multiplier;
        }

        public int getMonthsToComplete() {
            return monthsToComplete;
        }

        public double getQualityMultiplier() {
            return qualityMultiplier;
        }
    }

    public Game(String title, Genre genre, double budget, Company company, Market market) {
        this.title = title;
        this.genre = genre;
        this.budget = budget;
        this.company = company;
        this.market = market;
        this.developmentProgress = 0;
        this.quality = 0;
        this.completed = false;
    }

    public void develop(List<Employee> developers) {
        if (!completed) {
            // Double development speed
            int progress = developers.stream()
                    .mapToInt(Employee::getSkillLevel)
                    .sum() * 2;  // Added multiplier

            developmentProgress += progress;

            int totalRequired = genre.getMonthsToComplete() * PROGRESS_MULTIPLIER;
            double progressPercentage = (developmentProgress * 100.0) / totalRequired;

            System.out.printf("%s - Development Progress: %.2f%%\n", title, progressPercentage);

            if (developmentProgress >= totalRequired) {
                completed = true;
                calculateQuality(developers);
                System.out.println("\n" + title + " Completed!");
            }
        }
    }

    private void calculateQuality(List<Employee> developers) {
        double avgSkill = developers.stream()
                .mapToInt(Employee::getSkillLevel)
                .average()
                .orElse(0);
        quality = (int)(avgSkill * genre.getQualityMultiplier() * (budget / 10000));
    }

    public double calculateEarnings() {
        return GameEconomy.calculateGameRevenue(this, company.getShares(), market.getTotalMarketSize()) * 2;
    }

    public boolean isCompleted() { return completed; }
    public int  getProgress()  { return developmentProgress; }
    public int getQuality() { return quality; }
    public Genre getGenre() { return genre; }
    public String getTitle() { return title; }
    public double getBudget() { return budget; }
}
