import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobBoard {
    final private List<Employee> availableEmployees;
    private Random random;
    private NameGenerator nameGenerator;
    private Market market;

    public JobBoard(NameGenerator nameGenerator, Market market) {
        this.nameGenerator = nameGenerator;
        this.market = market;
        availableEmployees = new ArrayList<>();
        random = new Random();
        generateEmployees(150);
    }

    private void generateEmployees(int count) {
        for(int i = 0; i < count; i++) {
            Employee employee = createRandomEmployee();
            availableEmployees.add(employee);
        }
    }

    private Employee createRandomEmployee() {
        String name = nameGenerator.generateEmployeeName();
        int skillLevel = generateRandomSkillLevel();
        int productivity = generateRandomProductivity(skillLevel);
        double salary = calculateSalary(skillLevel, productivity);

                Employee employee = new Employee();

        employee.setName(name);
        employee.setSkillLevel(skillLevel);
        employee.setProductivity(productivity);
        employee.setSalary(salary);

        return employee;
    }

    private int generateRandomSkillLevel() {
        return random.nextInt(10) + 1;
    }

    private int generateRandomProductivity(int skillLevel) {
        int baseProductivity = 50 + (skillLevel * 5);
        int variation = random.nextInt(21) - 10;
        return baseProductivity + variation;
    }

    private double calculateSalary(int skillLevel, int productivity) {
        if (market.getCompanies() == null || market.getCompanies().isEmpty()) {
            return 3000 + (skillLevel * 750) * (1.0 + (productivity - 50) / 100.0);
        }

        double avgMarketRevenue = market.getCompanies().stream()
                .mapToDouble(Company::getQuarterlyRevenue)
                .average()
                .orElse(10000.0);

        return GameEconomy.calculateEmployeeSalary(
                skillLevel,
                productivity,
                avgMarketRevenue
        );
    }

    public List<Employee> getAvailableEmployees() {
        return availableEmployees;
    }


}


