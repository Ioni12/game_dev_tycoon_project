public class GameParameters {
    public final String title;
    public final Game.Genre genre;
    public final double budget;

    public GameParameters(String title, Game.Genre genre, double budget) {
        this.title = title;
        this.genre = genre;
        this.budget = budget;
    }
}
