import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayerCompany extends Company{
    private Scanner scan = new Scanner(System.in);
    private int reputation;
    private volatile boolean running = true;
    private Thread developmentThread;
    private Market market;


    public PlayerCompany(String name, Market market, NameGenerator nameGenerator) {
        super(name, market, 10, nameGenerator);
        this.marketShare = 10;
        employees = new ArrayList<>();
        games = new ArrayList<>();
    }

    protected void startNewGame() {
        System.out.println("enter the game title:");
        String title = scan.nextLine();

        System.out.println("Select genre");
        for(Game.Genre genre : Game.Genre.values()) {
            System.out.printf("%s (months: %d, quality multiplayer: %.1f)\n",
                    genre, genre.getMonthsToComplete(), genre.getQualityMultiplier());
        }

        String genreInput = scan.nextLine().toUpperCase();
        Game.Genre genre = Game.Genre.valueOf(genreInput);

        System.out.println("enter budget:");
        double budget = scan.nextDouble();
        scan.nextLine();

        if(budget <= funds) {
            games.add(new Game(title, genre, budget, this, market));
            funds -= budget;
            System.out.println("game development started");
        } else {
            System.out.println("insufficient funds");
        }
    }

    public void developGames() {
        if (games.isEmpty()) {
            System.out.println("no games in development");
            return;
        }


    }

    public void displayCompanyStatus() {
        System.out.println("\nCompany Status:");
        System.out.println("Name: " + name);
        System.out.println("Funds: $" + String.format("%.2f", funds));
        System.out.println("Number of employees: " + employees.size());

        if (!employees.isEmpty()) {
            System.out.println("\nCurrent Employees:");
            System.out.printf("%-20s %-10s %-10s%n", "Name", "Salary", "Skill Level");
            System.out.println("------------------------------------------------");
            for (Employee employee : employees) {
                System.out.printf("%-20s %-10.2f %-10d%n",
                        employee.getName(),
                        employee.getSalary(),
                        employee.getSkillLevel());
            }
        }
    }

    public void hireEmployees(List<Employee> availableEmployees) {
        if(availableEmployees.isEmpty()) System.out.println("the list is empty");
        System.out.println("enter the name of the employee tou want to hire");
        String name = scan.nextLine();

        boolean found = false;

        for(Employee employee: availableEmployees) {
            if(employee.getName().trim().equalsIgnoreCase(name.trim())) {
                employees.add(employee);
                availableEmployees.remove(employee);
                System.out.println("employee " + employee.getName() + " successfully hired");
                found = true;
                break;
            }
        }

        if(!found) {
            System.out.println("Employee with name \"" + name + "\" not found. Please try again.");
        }

    }

    public double getShares() {
        return marketShare;
    }

    public void setShares(double shares) {
        this.marketShare = shares;
    }
}
