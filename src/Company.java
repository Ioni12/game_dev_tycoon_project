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

    public Company(String name, Market market) {
        this.market = market;
        this.name = name;
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

    public void displayEmployees(List<Employee> availableEmployees) {
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

}
