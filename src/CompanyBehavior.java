public interface CompanyBehavior {
    double calculateGameBudget(double funds, double marketShare);
    Game.Genre selectGenre(double funds, double marketShare);
    boolean shouldStartNewGame(double funds, int currentGames);
    int getTargetEmployeeCount();
    double getMaxSalaryPercentage();
    String getBehaviorName();
}
