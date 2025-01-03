import java.util.*;
import java.util.stream.Collectors;

public class Market {
    private List<Employee> availableEmployees;
    private List<Company> companies;
    private PlayerCompany player;
    private Random random;
    private Scanner scan;
    private JobBoard board;
    private volatile boolean isRunning;
    private Thread marketThread;
    private final long marketCycle = 30000;
    private double TOTAL_MARKET_SHARE = 100;
    private int companyCount = 15;
    private NameGenerator nameGenerator;
    private double totalMarketSize = GameEconomy.BASE_MARKET_SIZE;
    private static final double MIN_VIABLE_MARKET_SHARE = 1.0;
    private static final double MAX_SHARE_CHANGE_PER_QUARTER = 2.0;
    private static final int MIN_COMPANIES = 5;


    public Market(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        isRunning = true;
        board  = new JobBoard(nameGenerator, this);
        availableEmployees  = Collections.synchronizedList(new ArrayList<>(board.getAvailableEmployees()));
        random = new Random();
        scan = new Scanner(System.in);
        menu();
    }


    private void updateMarketCycle() {
        System.out.println("the market is running");
        updateMarketShares();
        updateCompanyFinances();
        displayMarket();
    }

    private void checkCompanyBankruptcy() {
        Iterator<Company> iterator = companies.iterator();
        while (iterator.hasNext()) {
            Company company = iterator.next();
            if (company.getFunds() <= 0 && !(company instanceof PlayerCompany)) {
                handleCompanyBankruptcy((RivalCompany)company);
                iterator.remove();
            }
        }
    }

    private void handleCompanyBankruptcy(RivalCompany company) {
        System.out.println("\n" + company.getName() + " has gone bankrupt and is shutting down!");

        // Return employees to available pool
        if (company.employees != null) {
            availableEmployees.addAll(company.employees);
            System.out.println(company.employees.size() + " employees returned to job market");
        }

        // Redistribute market share
        double failedCompanyShare = company.getShares();
        redistributeMarketShare(failedCompanyShare);

        // Shutdown company threads
        company.shutdown();
    }

    private void redistributeMarketShare(double availableShare) {
        List<Company> eligibleCompanies = companies.stream()
                .filter(c -> !(c instanceof PlayerCompany))
                .filter(c -> c.getShares() >= MIN_VIABLE_MARKET_SHARE)
                .collect(Collectors.toList());

        double sharePerCompany = availableShare / eligibleCompanies.size();
        eligibleCompanies.forEach(c -> c.adjustMarketShare(sharePerCompany));
    }

    private void updateMarketShares() {
        double totalShares = companies.stream()
                .mapToDouble(Company::getShares)
                .sum();

        // Normalize shares if total isn't 100%
        if (Math.abs(totalShares - 100.0) > 0.01) {
            double multiplier = 100.0 / totalShares;
            companies.forEach(c -> c.setShares(c.getShares() * multiplier));
        }

        // Calculate performance-based share changes
        double avgPerformance = companies.stream()
                .mapToDouble(this::calculateCompanyPerformance)
                .average()
                .orElse(0.0);

        Map<Company, Double> shareChanges = new HashMap<>();
        double totalShareChange = 0;

        for (Company company : companies) {
            double performance = calculateCompanyPerformance(company);
            double relativePerformance = performance / avgPerformance - 1.0;
            double shareChange = Math.max(-MAX_SHARE_CHANGE_PER_QUARTER,
                    Math.min(MAX_SHARE_CHANGE_PER_QUARTER, relativePerformance * 2.0));

            shareChanges.put(company, shareChange);
            totalShareChange += shareChange;
        }

        // Apply changes while maintaining total of 100%
        for (Company company : companies) {
            double newShare = company.getShares() + shareChanges.get(company);
            newShare = Math.max(MIN_VIABLE_MARKET_SHARE, newShare);
            company.setShares(newShare);
        }

        // Final normalization
        totalShares = companies.stream().mapToDouble(Company::getShares).sum();
        double finalMultiplier = 100.0 / totalShares;
        companies.forEach(c -> c.setShares(c.getShares() * finalMultiplier));
    }

    private void updateCompanyFinances() {
        companies.forEach(company -> {
            if (company instanceof RivalCompany) {
                // Guaranteed base revenue plus market share bonus
                double baseRevenue = GameEconomy.calculateBaseQuarterlyRevenue(company.getShares());
                company.adjustFunds(baseRevenue);

                // Additional revenue from active games
                company.games.stream()
                        .filter(Game::isCompleted)
                        .forEach(game -> {
                            double gameRevenue = game.calculateEarnings();
                            company.adjustFunds(gameRevenue);
                        });
            }
        });
    }

    private void createMarket() {
        System.out.println("creating a market");
        companies = Collections.synchronizedList(new ArrayList<>());
        while(companies.size() < companyCount) {
            String name = nameGenerator.getRandomCompanyName();
            double share = calculateMarketShare(companyCount);
            if(!nameGenerator.isCompanyNameAvailable(name)) {
                RivalCompany company = new RivalCompany(name, this, share, nameGenerator);
                companies.add(company);
            }
        }
    }

    public void displayMarket() {
        for(Company company: companies) {
            company.displayCompanyStatus();
        }
    }

    private double calculateMarketShare(double companyCount) {
                double playerShare = player != null ? player.getShares() : 0;
                return TOTAL_MARKET_SHARE / (companyCount - playerShare);
    }

    public void setPlayer(PlayerCompany player) {
        this.player = player;
    }

    public List<Employee> getAvailableEmployees() {
        System.out.println("Available employees in market: " +
                (availableEmployees != null ? availableEmployees.size() : "null"));
        return availableEmployees;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    private void runSimulation() {
        marketThread = new Thread(() -> {
            while(isRunning) {
                try {
                    if(companies.isEmpty()) {
                        System.out.println("the companies list is empty");
                        continue;
                    }
                    for(Company company: companies) {
                        if(company instanceof RivalCompany) {
                            ((RivalCompany) company).initializeDevelopmentThread();
                        }
                    }

                    Thread.sleep(marketCycle);
                    checkCompanyBankruptcy();
                    updateMarketShares();
                    processQuarterlyUpdates();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        marketThread.setDaemon(true);
        marketThread.start();
    }

    private void processQuarterlyUpdates() {
        // Grow market size
        totalMarketSize *= (1 + GameEconomy.MARKET_GROWTH_RATE);

        // Calculate industry average performance
        double avgPerformance = companies.stream()
                .mapToDouble(this::calculateCompanyPerformance)
                .average()
                .orElse(0.0);

        // Update each company
        for (Company company : companies) {
            double performance = calculateCompanyPerformance(company);
            double shareChange = GameEconomy.calculateMarketShareChange(
                    company.getShares(),
                    performance,
                    avgPerformance
            );
            company.adjustMarketShare(shareChange);
            company.updateQuarterlyFinances();
        }
    }

    private double calculateCompanyPerformance(Company company) {
        return (company.getQuarterlyRevenue() * 0.4) +
                (company.games.stream().mapToDouble(Game::getQuality).average().orElse(0.0) * 200) +
                (company.employees.stream().mapToDouble(Employee::getSkillLevel).average().orElse(0.0) * 100) +
                (company.games.size() * 2000);
    }

    public double getTotalMarketSize() {
        return totalMarketSize;
    }

    private void menu() {
        createMarket();
        while(isRunning) {
            int answer;
            System.out.println("what do you want to do");
            System.out.println("1 -- display market");
            System.out.println("2 -- make games");
            answer = scan.nextInt();
            switch (answer) {
                case 1: displayMarket(); break;
                case 2: runSimulation(); break;
            }
        }
    }


    public void shutdown() {
        isRunning = false;
        if (marketThread != null) {
            marketThread.interrupt();
        }
        // Shutdown all rival companies
        companies.forEach(company -> {
            if (company instanceof RivalCompany) {
                ((RivalCompany) company).shutdown();
            }
        });
    }


}
