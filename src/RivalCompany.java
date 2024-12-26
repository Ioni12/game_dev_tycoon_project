import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RivalCompany extends Company{
    private final AIStrategy aiStrategy;

    public RivalCompany(String name, Market market, AIStrategy aiStrategy) {
        super(name, market);
        this.funds = random.nextDouble(200000);
        this.aiStrategy = aiStrategy;
    }

    public void companyLife() {


    }

    protected void startNewGame() {
        GameParameters params = aiStrategy.decideGameParameters(funds);

        if(params.budget <= funds) {
            games.add(new Game(params.title, params.genre, params.budget));
            funds -= params.budget;
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
        GameDevelopmentManager.develop(games, employees, running, developmentThread);
    }

    public void hireEmployees(List<Employee> availableEmployees) {
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

}
