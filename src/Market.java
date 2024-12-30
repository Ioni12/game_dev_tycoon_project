import java.util.*;

public class Market {
    private List<Company> companies;
    private PlayerCompany player;
    private Random random;
    private JobBoard board;
    private List<Employee> availableEmployees;
    private Scanner scan;
    private volatile boolean isRunning;
    private Thread marketThread;
    private final long marketCycle = 30000;
    private double TOTAL_MARKET_SHARE = 100;
    private int companyCount = 3;
    private NameGenerator nameGenerator;


    public Market(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
        isRunning = true;
        board  = new JobBoard(nameGenerator);
        availableEmployees = board.getAvailableEmployees();
        random = new Random();
        scan = new Scanner(System.in);
        System.out.println("the market is made");
        initializeMarket();
        createMarket();
        displayMarket();
    }

    private void initializeMarket() {
        System.out.println("we are initializing the market");
        marketThread = new Thread(() -> {
           while(isRunning) {
                try {
                    updateMarketCycle();
                    Thread.sleep(marketCycle);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("this is after running the market cycle");
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
        System.out.println("updating the market shares");
        double totalShare = 100;
        double playerShare = player != null ? player.getShares() : 0;
        double remainingShares = totalShare - playerShare;

        for(Company company: companies) {
            if(company instanceof RivalCompany) {
                double shareChange = (random.nextDouble() - 0.5) * 2;
                company.adjustMarketShare(shareChange);
            }
        }

        normalizeMarketShares(remainingShares);
    }

    private void normalizeMarketShares(double targetTotal) {
        System.out.println("normalizing the market shares");
        double currentTotal = companies.stream().mapToDouble(Company::getShares).sum();

        double factor = targetTotal / currentTotal;
        companies.forEach(company -> company.setShares(company.getShares() * factor));
    }

    private void updateCompanyFinances() {
        System.out.println("updating the company finances");
        companies.forEach(company -> {
            if(company instanceof RivalCompany) {
                double revenue = company.getShares() * 1000;
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
//        for(Company company: companies) {
//            company.displayCompanyStatus();
//        }
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
