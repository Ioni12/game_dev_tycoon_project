import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;

public class RivalCompany extends Company{
    private Random random;
    private Thread developmentThread;
    private CompanyBehavior behavior;

    private volatile boolean isRunning;

    private static final double MIN_BUDGET_THRESHOLD = 500; // Minimum budget to start a game
    private static final double BUDGET_RECOVERY_RATE = 10000;
    private static final long GAME_START_COOLDOWN = 30000;

    private long lastGameStartTime = 0;

    public RivalCompany(String name, Market market, double marketShare, NameGenerator nameGenerator) {
        super(name, market, marketShare, nameGenerator);
        random = new Random();
        this.funds = random.nextDouble(100000);
        isRunning = true;
        assignBehavior();
        entityLifeCycle();
    }

    public void initializeDevelopmentThread() {
        developmentThread = new Thread(() -> {
            while(isRunning) {
                try {
                    if (funds <= 0) {
                        System.out.println(getName() + " is out of funds!");
                        break;  // Exit the thread loop
                    }

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

    private void assignBehavior() {
        // Randomly assign a behavior type to this company
        double rand = Math.random();
        if (rand < 0.25) {
            behavior = new AggressiveBehavior();
        } else if (rand < 0.5) {
            behavior = new ConservativeBehavior();
        } else if (rand < 0.75) {
            behavior = new BalancedBehavior();
        } else {
            behavior = new InnovativeBehavior();
        }
        System.out.println(getName() + " is using " + behavior.getBehaviorName() + " strategy");
    }

    protected void startNewGame() {
        if (!behavior.shouldStartNewGame(funds, games.size())) return;

        double budget = behavior.calculateGameBudget(funds, marketShare);
        String title = nameGenerator.generateGameTitle();
        Game.Genre genre = behavior.selectGenre(funds, marketShare);

        Game game = new Game(title, genre, budget, this, market);
        games.add(game);
        adjustFunds(-budget);
    }

    private double calculateGameBudget() {
        double baseBudget = funds * 0.4;

        double marketMultiplier = 1.0 + (marketShare / 100);

        double randomFactor = 0.8 + (random.nextDouble() * 0.4);

        System.out.println("the budget is -- " + baseBudget * marketMultiplier * randomFactor);
        return baseBudget * marketMultiplier * randomFactor;
    }

    public void developGames() {
        Iterator<Game> iterator = games.iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (!game.isCompleted()) {
                double costs = GameEconomy.calculateDevelopmentCosts(game, employees);
                adjustFunds(-costs);
                game.develop(employees);
            } else {
                double earnings = game.calculateEarnings();
                adjustFunds(earnings);
                iterator.remove();
            }
        }

        if (behavior.shouldStartNewGame(funds, games.size())) {
            startNewGame();
        }
    }

    public void hireEmployees(List<Employee> availableEmployees) {
        int targetCount = behavior.getTargetEmployeeCount();

        while (employees.size() > targetCount) {
            Employee excessEmployee = employees.remove(random.nextInt(employees.size()));
            adjustFunds(excessEmployee.getSalary());
            System.out.println(String.format("%s removed %s (Skill: %d, Salary: $%.2f) to meet the target count.",
                    getName(), excessEmployee.getName(), excessEmployee.getSkillLevel(), excessEmployee.getSalary()));
        }


        while (employees.size() < targetCount) {
            if (availableEmployees == null || availableEmployees.isEmpty()) {
                System.out.println("No available employees to hire.");
                return;
            }

            double maxAffordableSalary = Math.min(funds * behavior.getMaxSalaryPercentage(), 10000);
            List<Employee> affordableCandidates = availableEmployees.stream()
                    .filter(emp -> emp.getSalary() <= maxAffordableSalary)
                    .collect(Collectors.toList());

            if (!affordableCandidates.isEmpty()) {
                int randomIndex = random.nextInt(affordableCandidates.size());
                Employee selectedEmployee = affordableCandidates.get(randomIndex);

                if (employees.add(selectedEmployee)) {
                    availableEmployees.remove(selectedEmployee);
                    adjustFunds(-selectedEmployee.getSalary());
                    System.out.println(String.format(
                            "%s (%s) hired %s (Skill: %d, Salary: $%.2f)",
                            getName(), behavior.getBehaviorName(), selectedEmployee.getName(),
                            selectedEmployee.getSkillLevel(), selectedEmployee.getSalary()
                    ));
                }
            } else {
                break;
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
        return Math.min(revenue, game.getBudget() * (1.0 + (game.getQuality() / 200.0)));
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
