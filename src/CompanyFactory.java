public class CompanyFactory {
    private final Market market;

    public CompanyFactory(Market market) {
        this.market = market;
    }

    public PlayerCompany createPlayerCompany(String name) {
        return new PlayerCompany(name,  market);
    }

    public RivalCompany createAICompany(String name, AIStrategy strategy) {
        return new RivalCompany(name, market, strategy);
    }
}
