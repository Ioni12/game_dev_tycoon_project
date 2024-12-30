import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class Company {
    protected Scanner scan = new Scanner(System.in);
    protected Random random;
    protected String name;
    protected double funds;
    protected int reputation;
    protected volatile boolean running = true;
    protected Thread developmentThread;
    protected List<Employee> employees;
    protected List<Game> games;
    protected Market market;
    protected double marketShare;
    protected NameGenerator nameGenerator;

    private static final double MIN_MARKET_SHARE = 0.0;
    private static final double MAX_MARKET_SHARE = 100.0;

    public Company(String name, Market market, double marketShare, NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        this.market = market;
        this.name = name;
        this.marketShare = marketShare;
        this.employees = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    public void companyLife() {
       boolean running = true;
       while(running) {
           displayMenu();
           int answer = getMenuChoice();
           running = handleMenuChoice(answer);
       }
    }

    protected void displayMenu() {
        System.out.println("\nWhat do you want to do:");
        System.out.println("1----> hire");
        System.out.println("2----> see status");
        System.out.println("3----> start new game");
        System.out.println("4----> develop games");
        System.out.println("5----> exit");
    }

    protected int getMenuChoice() {
        int answer = scan.nextInt();
        scan.nextLine();
        return answer;
    }

    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1: handleHiring(); break;
            case 2: displayCompanyStatus(); break;
            case 3: startNewGame(); break;
            case 4: developGames(); break;
            case 5: return false;
            default:
                System.out.println("invalid option, please try again");
        }
        return true;
    }

    protected void handleHiring() {
        displayEmployees(market.getAvailableEmployees());
        hireEmployees(market.getAvailableEmployees());
    }

    protected abstract void startNewGame();

    protected abstract void developGames();

    protected void displayCompanyStatus() {
        System.out.println("who is activating this -----");
        System.out.println("\nCompany Status:");
        System.out.println("Name: " + name);
        System.out.println("Funds: $" + String.format("%.2f", funds));
        System.out.println("Number of employees: " + this.employees.size());
        System.out.println("the games: " + games.size());

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

    protected void displayEmployees(List<Employee> availableEmployees) {
        System.out.println("select your employees: ");
        System.out.printf("%-20s %-10s %-10s%n", "Name", "Salary", "Skill Level");
        System.out.println("------------------------------------------------");

        for (Employee employee : availableEmployees) {
            System.out.printf("%-20s %-10.2f %-10d%n",
                    employee.getName(),
                    employee.getSalary(),
                    employee.getSkillLevel());
        }
    }

    protected abstract void hireEmployees(List<Employee> availableEmployees);

    protected double getFunds() { return this.funds; }

    protected String getName() { return this.name; }

    public double getShares() {
        return this.marketShare;
    }

    public void setShares(double newShare) {
        this.marketShare = clampMarketShare(newShare);
    }

    public void adjustMarketShare(double change) {
        this.marketShare = clampMarketShare(this.marketShare + change);
    }

    private double clampMarketShare(double share) {
        return Math.min(Math.max(share, MIN_MARKET_SHARE), MAX_MARKET_SHARE);
    }

    public void adjustFunds(double amount) {
        this.funds += amount;
        // Optional: Prevent negative funds if that's a requirement
        if (this.funds < 0) {
            this.funds = 0;
        }
    }

    public void setFunds(double newAmount) {
        this.funds = Math.max(newAmount, 0);
    }

}
