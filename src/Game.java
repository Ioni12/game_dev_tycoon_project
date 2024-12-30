import java.util.List;

public class Game {
    private String title;
    private Genre genre;
    private double budget;
    private int developmentProgress;
    private int quality;
    private boolean completed;
    private static final int PROGRESS_MULTIPLIER = 100;

    public enum Genre {
        ACTION(12, 1.2),
        CASUAL(6, 0.8),
        RPG(18, 1.5),
        STRATEGY(10, 1.0),
        SIMULATION(8, 0.9);

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

    public Game(String title, Genre genre, double budget) {
        this.title = title;
        this.genre = genre;
        this.budget = budget;
        this.developmentProgress = 0;
        this.quality = 0;
        this.completed = false;
    }

    public void develop(List<Employee> developers) {
        if (!completed) {
            // Calculate progress from developers
            int progress = developers.stream()
                    .mapToInt(Employee::getSkillLevel)
                    .sum();

            developmentProgress += progress;

            // Calculate and display progress percentage
            int totalRequired = genre.getMonthsToComplete() * PROGRESS_MULTIPLIER;
            double progressPercentage = (developmentProgress * 100.0) / totalRequired;

            // Format to 2 decimal places
            System.out.printf("%s - Development Progress: %.2f%%\n", title, progressPercentage);

            // Check if game is completed
            if (developmentProgress >= totalRequired) {
                completed = true;
                calculateQuality(developers);
                System.out.println("\n" + title + " Completed!");
            }
        }
    }

    public double getProgressPercentage() {
        int totalRequired = genre.getMonthsToComplete() * PROGRESS_MULTIPLIER;
        return (developmentProgress * 100.0) / totalRequired;
    }

    private void calculateQuality(List<Employee> developers) {
        double avgSkill = developers.stream()
                .mapToInt(Employee::getSkillLevel)
                .average()
                .orElse(0);
        quality = (int)(avgSkill * genre.getQualityMultiplier() * (budget / 10000));
    }

    public boolean isCompleted() { return completed; }
    public int  getProgress()  { return developmentProgress; }
    public int getQuality() { return quality; }
    public Genre getGenre() { return genre; }
    public String getTitle() { return title; }
}
