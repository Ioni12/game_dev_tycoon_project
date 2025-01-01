import java.util.*;

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


    public Market(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        isRunning = true;
        board  = new JobBoard(nameGenerator);
        availableEmployees = board.getAvailableEmployees();
        random = new Random();
        scan = new Scanner(System.in);
        menu();
    }

    private void initializeMarket() {
        marketThread = new Thread(() -> {
           while(isRunning) {
                try {
                    updateMarketCycle();
                    Thread.sleep(marketCycle);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    break;
                }
           }
        });
        marketThread.setName("Market-cycle");
        marketThread.setDaemon(true);
        marketThread.start();
    }

    private void updateMarketCycle() {
        System.out.println("the market is running");
        updateMarketShares();
        updateCompanyFinances();
        displayMarket();
    }

    private void updateMarketShares() {
        Map<Company, Double> performanceScores = new HashMap<>();
        double totalPerformance = 0;

        // Calculate performance scores based on game quality, revenue, and employee skill
        for (Company company : companies) {
            double gameQualityScore = company.games.stream()
                    .mapToDouble(Game::getQuality)
                    .average()
                    .orElse(0.0);

            double revenueScore = company.getFunds() / 10000.0; // Normalize large numbers

            double employeeScore = company.employees.stream()
                    .mapToInt(Employee::getSkillLevel)
                    .average()
                    .orElse(0.0);

            // Weight different factors
            double performanceScore = (gameQualityScore * 0.4) +
                    (revenueScore * 0.4) +
                    (employeeScore * 0.2);

            // Add some randomness (±20%)
            performanceScore *= (0.8 + random.nextDouble() * 0.4);

            performanceScores.put(company, performanceScore);
            totalPerformance += performanceScore;
        }

        // Redistribute market shares based on performance
        if (totalPerformance > 0) {
            double remainingShare = TOTAL_MARKET_SHARE -
                    (player != null ? player.getShares() : 0);

            for (Company company : companies) {
                if (company != player) {
                    double newShare = (performanceScores.get(company) / totalPerformance) *
                            remainingShare;
                    company.setShares(newShare);
                }
            }
        }
    }

    private void updateCompanyFinances() {
        System.out.println("updating the company finances");
        companies.forEach(company -> {
            if(company instanceof RivalCompany) {
                double revenue = company.getShares() * 100;
                company.adjustFunds(revenue);
            }
        });
    }

    private void createMarket() {
        System.out.println("creating a market");
        companies = new ArrayList<>();
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
        for (Company company : companies) {
            company.updateQuarterlyFinances();  // Update finances, taxes, and market share for each company
        }
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
