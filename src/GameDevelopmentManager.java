import java.util.List;

public class GameDevelopmentManager {
    public static void develop(List<Game> games, List<Employee> employees,
                               boolean running, Thread developmentThread) {
        if(games.isEmpty()) return;

        developmentThread = new Thread(() -> {
            for(Game game: games) {
                while(running && !game.isCompleted()) {
                    game.develop(employees);
                    System.out.printf("%s - progress: %d%% %s%n",
                            game.getTitle(),
                            game.getProgress() / game.getGenre().getMonthsToComplete(),
                            game.isCompleted() ? "Completed quality: " + game.getQuality(): "");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                if(!running) break;
                System.out.println("game completed");
            }
        });
        developmentThread.start();

        try {
            developmentThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
