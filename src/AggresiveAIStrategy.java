import java.util.Random;

public class AggresiveAIStrategy implements AIStrategy{
    private final Random random = new Random();
    @Override
    public GameParameters decideGameParameters(double availableFunds) {
        double budgetPercentage = 0.6 + (random.nextDouble() * 0.2);
        double budget = availableFunds + budgetPercentage;

        Game.Genre[] genres = Game.Genre.values();
        Game.Genre selectedGenre = genres[random.nextInt(genres.length)];

        String title = generateGameTitle();

        return new GameParameters(title, selectedGenre, budget);
    }

    private String generateGameTitle() {
        String[] prefixes = {"Super", "Mega", "Ultra", "Epic", "Legendary"};
        String[] words = {"Quest", "Warriors", "Adventure", "Legend", "Battle"};

        return prefixes[random.nextInt(prefixes.length)] + " " + words[random.nextInt(words.length)];
    }
}
