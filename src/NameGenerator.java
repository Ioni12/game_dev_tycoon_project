import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NameGenerator {
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

    private static final String[] GAME_PREFIXES = {
            "Crystal", "Dark", "Epic", "Infinite", "Lost",
            "Mystic", "Nova", "Omega", "Prime", "Shadow"
    };

    private static final String[] GAME_SUFFIXES = {
            "Chronicles", "Legacy", "Legends", "Wars", "World",
            "Adventure", "Saga", "Quest", "Empire", "Heroes"
    };


    private Set<String> usedCompanyNames;
    private Random random;

    public NameGenerator() {
        this.usedCompanyNames = new HashSet<>();
        this.random = new Random();
        System.out.println("the name generator is made");
    }

    public String getRandomCompanyName() {
        String name;
        do {
            name = GAME_COMPANIES[random.nextInt(GAME_COMPANIES.length)];
        } while (usedCompanyNames.contains(name));

        usedCompanyNames.add(name);
        return name;
    }

    public String generateEmployeeName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }

    public String generateGameTitle() {
        String prefix = GAME_PREFIXES[random.nextInt(GAME_PREFIXES.length)];
        String suffix = GAME_SUFFIXES[random.nextInt(GAME_SUFFIXES.length)];
        return prefix + " " + suffix;
    }


    public void releaseCompanyName(String name) {
        usedCompanyNames.remove(name);
    }

    public boolean isCompanyNameAvailable(String name) {
        return !usedCompanyNames.contains(name);
    }

    public void reset() {
        usedCompanyNames.clear();
    }
}
