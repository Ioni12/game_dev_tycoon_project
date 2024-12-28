import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobBoard {
    final private List<Employee> availableEmployees;
    private Random random;

    private static final String[] FIRST_NAMES = {
            "John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Ava",
            "Alexander", "Isabella", "Daniel", "Mia", "David", "Charlotte", "Joseph",
            "Amelia", "Andrew", "Harper", "Lucas", "Evelyn", "Gabriel", "Abigail",
            "Samuel", "Emily", "Benjamin", "Elizabeth", "Henry", "Sofia", "Matthew",
            "Avery"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
            "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark",
            "Ramirez", "Lewis", "Robinson"
    };

    public JobBoard() {
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
        String name = generateRandomName();
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

    private String generateRandomName() {
        String first_name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last_name = LAST_NAMES[random.nextInt(LAST_NAMES.length)];

        return first_name + " " + last_name;
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
        double baseSalary = 30000 + (skillLevel * 5000) + (productivity * 200);
        double variation = (random.nextDouble() * 0.1) - 0.05;
        return Math.round(baseSalary + (1 + variation));
    }

    public List<Employee> getAvailableEmployees() {
        return new ArrayList<>(availableEmployees);
    }

}


