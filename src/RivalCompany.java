import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class RivalCompany extends Company{
    private Random random;
    private volatile boolean isRunning;
    private Thread developmentThread;


    public RivalCompany(String name, Market market, double marketShare) {
        super(name, market, marketShare);
        random = new Random();
        this.funds = random.nextDouble(200000);
        isRunning = true;
        initializeDevelopmentThread();
    }

    private void initializeDevelopmentThread() {
        developmentThread = new Thread(() -> {
            while(isRunning) {
                try {
                    developGames();
                    Thread.sleep(random.nextInt(10000)+ 5000);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        developmentThread.setName("development " + getName());
        developmentThread.setDaemon(true);
        developmentThread.start();
    }

    public void shutdown() {
        isRunning = false;
        if (developmentThread != null) {
            developmentThread.interrupt();
        }
    }

    public void companyLife() {

    }

    protected void startNewGame() {

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
