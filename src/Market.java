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
    private double totalMarketSize = GameEconomy.BASE_MARKET_SIZE;


    public Market(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        isRunning = true;
        board  = new JobBoard(nameGenerator, this);
        availableEmployees  = Collections.synchronizedList(new ArrayList<>(board.getAvailableEmployees()));
        random = new Random();
        scan = new Scanner(System.in);
        menu();
    }

//    private void initializeMarket() {
//        marketThread = new Thread(() -> {
//           while(isRunning) {
//                try {
//                    updateMarketCycle();
//                    Thread.sleep(marketCycle);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//
//                    break;
//                }
//           }
//        });
//        marketThread.setName("Market-cycle");
//        marketThread.setDaemon(true);
//        marketThread.start();
//    }

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
        // Calculate total current shares of remaining companies (excluding player)
        double totalCurrentShares = companies.stream()
                .filter(c -> !(c instanceof PlayerCompany))
                .mapToDouble(Company::getShares)
                .sum();

        if (totalCurrentShares > 0) {
            // Distribute proportionally based on current market share
            for (Company company : companies) {
                if (!(company instanceof PlayerCompany)) {
                    double proportion = company.getShares() / totalCurrentShares;
                    double newShare = company.getShares() + (availableShare * proportion);
                    company.setShares(newShare);
                }
            }
        }
        System.out.println("Market share of " + String.format("%.2f", availableShare) + "% has been redistributed");
    }

    private void updateMarketShares() {
        Map<Company, Double> performanceScores = new HashMap<>();
        double totalPerformance = 0.001; // Prevent division by zero

        for (Company company : companies) {
            // Base score so companies always have some performance
            double baseScore = 1.0;

            double gameQualityScore = company.games.stream()
                    .mapToDouble(Game::getQuality)
                    .average()
                    .orElse(0.0);

            double revenueScore = company.getFunds() / 10000.0;

            double employeeScore = company.employees.stream()
                    .mapToInt(Employee::getSkillLevel)
                    .average()
                    .orElse(0.0);

            // Add baseScore to ensure some minimal performance
            double performanceScore = baseScore +
                    (gameQualityScore * 0.4) +
                    (revenueScore * 0.4) +
                    (employeeScore * 0.2);

            // Add more randomness to create market dynamics
            performanceScore *= (0.5 + random.nextDouble());

            performanceScores.put(company, performanceScore);
            totalPerformance += performanceScore;
        }

        // Redistribute market shares
        double remainingShare = TOTAL_MARKET_SHARE -
                (player != null ? player.getShares() : 0);

        for (Company company : companies) {
            if (company != player) {
                double newShare = (performanceScores.get(company) / totalPerformance) *
                        remainingShare;
                // Add maximum change limit to prevent drastic swings
                double currentShare = company.getShares();
                double maxChange = 2.0; // Maximum 2% change per cycle
                newShare = Math.max(currentShare - maxChange,
                        Math.min(currentShare + maxChange, newShare));
                company.setShares(newShare);
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
        double revenue = company.getQuarterlyRevenue();
        double gameQuality = company.games.stream()
                .mapToDouble(Game::getQuality)
                .average()
                .orElse(0.0);

        double employeeSkill = company.employees.stream()
                .mapToDouble(Employee::getSkillLevel)
                .average()
                .orElse(0.0);

        int activeGames = (int) company.games.stream()
                .filter(g -> !g.isCompleted())
                .count();

        return (revenue * 0.4) +
                (gameQuality * 100 * 0.3) +
                (employeeSkill * 50 * 0.2) +
                (activeGames * 1000 * 0.1);
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
