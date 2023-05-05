import bg.sofia.uni.fmi.mjt.cocktail.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(6666);
        server.start();


    }
}