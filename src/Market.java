import java.util.*;

public class Market {
    private List<Company> companies;
    private Set<String> usedNames = new HashSet<>();
    private Random random;
    private JobBoard board;
    private List<Employee> availableEmployees;
    private Scanner scan;
    private AIStrategy aiStrategy;

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
        while(companies.size() < 10) {
            String name = GAME_COMPANIES[random.nextInt(GAME_COMPANIES.length)];
            if(!usedNames.contains(name)) {
                RivalCompany company = new RivalCompany(name, this, aiStrategy);
                companies.add(company);
            }
        }
    }

    public void displayMarket() {
        for(Company company: companies) {
            company.displayCompanyStatus();
        }
    }

    public void displayEmployees(List<Employee> availableEmployees) {
        System.out.println("select your employees: ");
        System.out.printf("%-20s %-10s %-10s%n", "Name", "Salary", "Skill Level");
        System.out.println("------------------------------------------------");

        for (Employee employee : availableEmployees) {
            System.out.printf("%-20s %-10.2f %-10d%n",
                    employee.getName(),
                    employee.getSalary(),
                    employee.getSkillLevel());
        }
    }

    public List<Employee> getAvailableEmployees() {
        return availableEmployees;
    }
}
