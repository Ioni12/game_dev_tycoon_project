import java.util.List;

public interface HiringStrategy {
    boolean hireEmployee(Company company, Employee employee);
    List<Employee> filterCandidates(List<Employee> candidates, Company company);
}
