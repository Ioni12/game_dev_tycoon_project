import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobBoard {
    final private List<Employee> availableEmployees;
    private Random random;
    private NameGenerator nameGenerator;

    public JobBoard(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        availableEmployees = new ArrayList<>();
        random = new Random();
        generateEmployees(50);
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
        double baseSalary = 3000 + (skillLevel * 500) + (productivity * 20);  // Adjusted formula
        double variation = (random.nextDouble() * 0.2) - 0.1;  // ±10% variation
        return Math.round(baseSalary * (1 + variation));
    }

    public List<Employee> getAvailableEmployees() {
        return availableEmployees;
    }


}


