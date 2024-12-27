import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MarketObserver {
    private Market market;
    private Timer marketUpdateTimer;

    public void startMonitoring(Market market) {
        this.market = market;
        scheduleMarketupdates();
    }

    private void scheduleMarketupdates() {
        marketUpdateTimer = new Timer();
        marketUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateMarketConditions();
            }
        }, 0, 5000);
    }

    private void updateMarketConditions() {
        updateCompanyValues();
        checkBanckruptCompanies();
        generateNewOpportunities();
    }

    private void updateCompanyValues() {
        for (Company company : market.getCompanies()) {

        }
    }

    private void checkBanckruptCompanies() {
        List<Company> companies = market.getCompanies();
        companies.removeIf(company -> {
            if(company.getFunds() <= 0) {
                notifyCompanyBankrupt(company);
                return true;
            }
            return false;
        });
    }

    private void generateNewOpportunities() {

    }

    public void notifyCompanyAdded(Company company) {
        System.out.println("new company entered the market: " + company.getName());
    }

    public void notifyCompanyRemoved(Company company) {
        System.out.println("company left the market: " + company.getName());
    }

    public void notifyCompanyBankrupt(Company company) {
        System.out.println("Company went bankrupt: " + company.getName());
    }
}
