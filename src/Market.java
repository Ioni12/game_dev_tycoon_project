import java.util.*;

public class Market {
    private List<Company> companies;
    private PlayerCompany player;
    private Set<String> usedNames = new HashSet<>();
    private Random random;
    private JobBoard board;
    private List<Employee> availableEmployees;
    private Scanner scan;
    private double TOTAL_MARKET_SHARE = 100;
    private int companyCount = 10;

    private static final String[] GAME_COMPANIES = {
            "Nintendo", "Sony Interactive Entertainment", "Microsoft", "Ubisoft",
            "Electronic Arts", "Activision Blizzard", "Square Enix", "Capcom",
            "Bethesda Softworks", "Valve Corporation", "Rockstar Games", "Epic Games",
            "Bandai Namco Entertainment", "SEGA", "CD Projekt Red", "2K Games",
            "Telltale Games", "Konami", "Blizzard Entertainment", "FromSoftware",
            "Gearbox Software", "Insomniac Games", "Naughty Dog", "Bungie",
            "Monolith Soft", "Treyarch", "Infinity Ward", "id Software", "Crytek",
            "Respawn Entertainment", "Obsidian Entertainment", "Rare",
            "Double Fine Productions", "IO Interactive", "Remedy Entertainment",
            "Supergiant Games", "PlatinumGames", "Arkane Studios", "Paradox Interactive",
            "Koei Tecmo", "Grasshopper Manufacture", "ZeniMax Online Studios",
            "Larian Studios", "Hi-Rez Studios", "Avalanche Studios",
            "Dontnod Entertainment", "Media Molecule", "Sucker Punch Productions",
            "Guerrilla Games", "Mojang Studios", "Cyan Worlds", "People Can Fly",
            "THQ Nordic", "Panic Button", "Crystal Dynamics", "Eidos-Montréal",
            "Hangar 13", "Bluepoint Games", "Asobo Studio", "Creative Assembly",
            "Firaxis Games", "Rocksteady Studios", "Polyphony Digital",
            "Camelot Software Planning", "Level-5", "Game Freak", "HAL Laboratory",
            "Intelligent Systems", "Tango Gameworks", "Clover Studio", "Team Ninja",
            "CyberConnect2", "DICE", "Vicarious Visions", "Neversoft", "Harmonix",
            "Playdead", "Thatgamecompany", "Team17", "Re-Logic", "Psyonix",
            "InXile Entertainment", "Warhorse Studios", "Raven Software", "Techland",
            "Saber Interactive", "Behaviour Interactive", "Red Barrels",
            "Frictional Games", "Starbreeze Studios", "Housemarque", "Ninja Theory",
            "Bend Studio", "Yacht Club Games", "DrinkBox Studios", "Facepunch Studios",
            "Sumo Digital", "Tarsier Studios", "Croteam", "WayForward Technologies",
            "Zindagi Games", "Iron Galaxy Studios", "Fatshark",
            "Cliffhanger Productions", "Overkill Software", "Hidden Path Entertainment"
    };


    public Market() {
        board  = new JobBoard();
        availableEmployees = board.getAvailableEmployees();
        random = new Random();
        scan = new Scanner(System.in);
        createMarket();
        displayMarket();
    }

    private void createMarket() {
        companies = new ArrayList<>();
        while(companies.size() < companyCount) {
            String name = GAME_COMPANIES[random.nextInt(GAME_COMPANIES.length)];
            double share = calculateMarketShare(companyCount);
            if(!usedNames.contains(name)) {
                RivalCompany company = new RivalCompany(name, this, share);
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
        return TOTAL_MARKET_SHARE / (companyCount - 10);
    }

    public void setPlayer(PlayerCompany player) {
        this.player = player;
    }

    public List<Employee> getAvailableEmployees() {
        return availableEmployees;
    }

    public List<Company> getCompanies() {
        return companies;
    }


}
