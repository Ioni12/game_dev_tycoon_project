import java.util.Scanner;

public class Start {
    private Scanner scan = new Scanner(System.in);
    private boolean running = true;
    private Market market;
    private PlayerCompany playerCompany;

    public Start() {
        market = new Market();
        playerCompany = new PlayerCompany(welcomingMessage(),  market);
        optionsMenu();
    }

    public void optionsMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\nWhat do you want to do:");
            System.out.println("1----> hire");
            System.out.println("2----> start new game");
            System.out.println("3----> develop games");
            System.out.println("4----> see the market");
            System.out.println("5----> see status");
            System.out.println("6----> exit");

            int answer = scan.nextInt();
            scan.nextLine();

            switch (answer) {
                case 1:
                    playerCompany.handleHiring();
                    break;
                case 2:
                    playerCompany.startNewGame();
                    break;
                case 3:
                    playerCompany.developGames();
                    break;
                case 4:
                    market.displayMarket();
                    break;
                case 5:
                    playerCompany.displayCompanyStatus();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("invalid option, please try again");
            }
        }
    }

    public String welcomingMessage() {
        System.out.println("if you want to create your game company you must enter a name for the company");
        String name = scan.nextLine();
        return name;
    }
}
