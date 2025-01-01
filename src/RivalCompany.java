import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;

public class RivalCompany extends Company{
    private Random random;
    private Thread developmentThread;

    private volatile boolean isRunning;

    private static final double MIN_BUDGET_THRESHOLD = 500; // Minimum budget to start a game
    private static final double BUDGET_RECOVERY_RATE = 10000;
    private static final long GAME_START_COOLDOWN = 30000;

    private long lastGameStartTime = 0;

    public RivalCompany(String name, Market market, double marketShare, NameGenerator nameGenerator) {
        super(name, market, marketShare, nameGenerator);
        random = new Random();
        this.funds = random.nextDouble(20000000);
        isRunning = true;
        entityLifeCycle();
    }

    public void initializeDevelopmentThread() {
        developmentThread = new Thread(() -> {
            while(isRunning) {
                try {
                    if(employees.isEmpty()) {
                        System.out.println(getName() + " has no employees, attempting to hire...");
                        hireEmployees(market.getAvailableEmployees());
                        Thread.sleep(5000);
                        continue;
                    }
                    developGames();
                    Thread.sleep(5000);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        developmentThread.setName("Development-" + getName());
        developmentThread.setDaemon(true);
        developmentThread.start();
    }


    protected void startNewGame() {
        if(employees.isEmpty()) {
            return;
        }

        double availableBudget = calculateGameBudget();
        if(availableBudget < MIN_BUDGET_THRESHOLD) {
            return;
        }

        String gameTitle = nameGenerator.generateGameTitle();
        Game.Genre genre = selectGenre();
        Game game = new Game(gameTitle, genre, availableBudget);

        games.add(game);
        adjustFunds(-availableBudget);
        lastGameStartTime = System.currentTimeMillis();

        System.out.println(String.format(
                "\n%s started development of '%s'\nGenre: %s\nBudget: $%.2f\nEstimated months: %d\nRemaining funds: $%.2f",
                getName(), gameTitle, genre, availableBudget, genre.getMonthsToComplete(), funds
        ));
    }

    private double calculateGameBudget() {
        double baseBudget = funds * 0.4;

        double marketMultiplier = 1.0 + (marketShare / 100);

        double randomFactor = 0.8 + (random.nextDouble() * 0.4);

        System.out.println("the budget is -- " + baseBudget * marketMultiplier * randomFactor);
        return baseBudget * marketMultiplier * randomFactor;
    }

    public void developGames() {
        if(games.size() < 2 && canStartNewGame() && random.nextDouble() < 0.5) {
            startNewGame();
        }

        // Process existing games
        Iterator<Game> iterator = games.iterator();
        while(iterator.hasNext()) {
            Game game = iterator.next();
            if(game.isCompleted()) {
                handleCompletedGame(game);
                iterator.remove();
            } else {
                game.develop(employees);
                double earnings = game.calculateEarnings();
                if(earnings > 0) {
                    adjustFunds(earnings);
                    System.out.printf("%s earned $%.2f from %s!\n", getName(), earnings, game.getTitle());
                }
            }
        }

        // Simulate budget recovery over time
        recoverBudget();
    }

    public void hireEmployees(List<Employee> availableEmployees) {
        while(employees.size() < 5) {
            System.out.println(getName() + " attempting to hire employees...");
            if (availableEmployees == null || availableEmployees.isEmpty()) {
                System.out.println("No available employees to hire.");
                return;
            }

            if (employees.size() >= 5) {
                System.out.println(getName() + " already has maximum employees.");
                return;
            }

            double maxAffordableSalary = Math.min(funds * 0.2, 10000);

            // Find suitable candidates within budget
            List<Employee> affordableCandidates = availableEmployees.stream()
                    .filter(emp -> emp.getSalary() <= maxAffordableSalary)
                    .collect(Collectors.toList());

            if (!affordableCandidates.isEmpty()) {
                // Randomly select one of the affordable candidates
                int randomIndex = random.nextInt(affordableCandidates.size());
                Employee selectedEmployee = affordableCandidates.get(randomIndex);

                // Hire the employee
                if (employees.add(selectedEmployee)) {
                    availableEmployees.remove(selectedEmployee);
                    adjustFunds(-selectedEmployee.getSalary());

                    System.out.println(getName() + " hired " + selectedEmployee.getName() +
                            " (Skill: " + selectedEmployee.getSkillLevel() +
                            ", Salary: $" + String.format("%.2f", selectedEmployee.getSalary()) + ")");
                }
            } else {
                System.out.println(getName() + " couldn't find affordable candidates. Max affordable salary: $" +
                        String.format("%.2f", maxAffordableSalary));
            }
        }
    }

    private void entityLifeCycle() {
        introduceYourSelf();
        hireEmployees(market.getAvailableEmployees());
    }

    private boolean canStartNewGame() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGameStartTime < GAME_START_COOLDOWN) {
            return false;
        }
        return funds >= MIN_BUDGET_THRESHOLD;
    }

    private void handleCompletedGame(Game game) {
        double revenue = calculateGameRevenue(game);
        adjustFunds(revenue);
        System.out.println(String.format(
                "\n%s completed game '%s'\nQuality: %d\nRevenue: $%.2f\nCurrent Funds: $%.2f",
                getName(),
                game.getTitle(),
                game.getQuality(),  // Changed from %.1f to %d since quality is an integer
                revenue,
                funds
        ));
    }

    private double calculateGameRevenue(Game game) {
        double baseRevenue = game.getBudget() * (1 + (game.getQuality() / 1000.0));
        double marketFactor = 1 + (marketShare / 1000.0);
        double revenue = baseRevenue * marketFactor * (0.8 + random.nextDouble() * 0.4);
        return Math.min(revenue, game.getBudget() * 1.5);
    }

    private void recoverBudget() {
        adjustFunds(BUDGET_RECOVERY_RATE * (marketShare / 100.0));
    }

    private void introduceYourSelf() {
        System.out.println(this.name + " is being made ready");
    }

    private Game.Genre selectGenre() {
        System.out.println("selecting genres");
        Game.Genre[] genres = Game.Genre.values();

        // Consider market share and company strengths when selecting genre
        if ( random.nextDouble() < 0.7) {
            // More likely to choose complex genres when market leader
            return genres[random.nextInt(3)]; // ACTION, RPG, or STRATEGY
        } else if (random.nextDouble() < 0.6) {
            // More likely to choose cheaper genres when low on funds
            return Game.Genre.CASUAL;
        } else {
            // Random selection
            return genres[random.nextInt(genres.length)];
        }
    }

    public void shutdown() {
        isRunning = false;
        if (developmentThread != null) {
            developmentThread.interrupt();
        }
    }


}
