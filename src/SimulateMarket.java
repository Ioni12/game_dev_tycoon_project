public class SimulateMarket {
    public static void main(String[] args) {
        System.out.println("starting with the simulation");
        NameGenerator nameGenerator = new NameGenerator();
        Market market = new Market(nameGenerator);
    }
}


/// to do the hiring proccess is a mess and needs a solution