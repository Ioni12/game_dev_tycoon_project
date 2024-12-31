import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;

public class RivalCompany extends Company{
    private Random random;
    private volatile boolean isRunning;
    private Thread developmentThread;



    public RivalCompany(String name, Market market, double marketShare, NameGenerator nameGenerator) {
        super(name, market, marketShare, nameGenerator);
        random = new Random();
        this.funds = random.nextDouble(20000000);
        isRunning = true;
        hireEmployees(market.getAvailableEmployees());
        initializeDevelopmentThread();
    }

    private void initializeDevelopmentThread() {
        developmentThread = new Thread(() -> {
            while(isRunning) {
                try {
                    // Check if we have employees
                    if(employees.isEmpty()) {
                        System.out.println(getName() + " has no employees, attempting to hire...");
                        hireEmployees(market.getAvailableEmployees());
                        Thread.sleep(5000); // Wait before trying again
                        continue;
                    }


                    // Develop existing games
                    if(!games.isEmpty()) {
                        System.out.println("\n" + getName() + " developing " + games.size() + " games:");
                        for(Game game : new ArrayList<>(games)) {
                            if(!game.isCompleted()) {
                                game.develop(employees);
                            }
                        }
                        // Remove completed games after development cycle
                        games.removeIf(game -> {
                            if(game.isCompleted()) {
                                System.out.println(getName() + "'s game " + game.getTitle() +
                                        " completed with quality rating: " + game.getQuality());
                                return true;
                            }
                            return false;
                        });
                    }

                    Thread.sleep(3000); // Development cycle every 3 seconds
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        developmentThread.setName("Development-" + getName());

        developmentThread.start();
    }


    protected void startNewGame() {
        if(employees.isEmpty()) {
            System.out.println(getName() + " cannot start a new game without employees.");
            return;
        }

        double availableBudget = calculateGameBudget();
        if(availableBudget < 10000) {
            System.out.println(getName() + " insufficient budget for new game.");
            return;
        }

        String gameTitle = nameGenerator.generateGameTitle();
        Game.Genre genre = selectGenre();
        Game game = new Game(gameTitle, genre, availableBudget);

        games.add(game);
        adjustFunds(-availableBudget);

        System.out.println("\n" + getName() + " started development of: " + gameTitle +
                "\nGenre: " + genre +
                "\nBudget: $" + String.format("%.2f", availableBudget) +
                "\nEstimated months: " + genre.getMonthsToComplete());
    }

    private double calculateGameBudget() {
        System.out.println(this.name + "is calculating the game budget");
        double baseBudget = funds * 0.4;

        double marketMultiplier = 1.0 + (marketShare / 100);

        double randomFactor = 0.8 + (random.nextDouble() * 0.4);

        System.out.println("the budget is -- " + baseBudget * marketMultiplier * randomFactor);
        return baseBudget * marketMultiplier * randomFactor;
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

    public void developGames() {
        System.out.println("in the beginning stages of developing games");
        if(games.size() < 2 && random.nextDouble() < 0.3) {
            startNewGame();
        }

        games.removeIf(Game::isCompleted);

        if(!games.isEmpty()) {
            for(Game game : games) {
                game.develop(employees);
                if(game.isCompleted()) {
                    System.out.println(name + "'s game " + game.getTitle() +
                            " completed with quality rating: " + game.getQuality());
                }
            }
        }
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

           double maxAffordableSalary = Math.min(funds * 0.05, 10000);

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


    public void shutdown() {
        isRunning = false;
        if (developmentThread != null) {
            developmentThread.interrupt();
        }
    }


}
