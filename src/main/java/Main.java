import application.App;
import service.DatabaseService;

public class Main {
    public static void main(String[] args) {
        String path = "src/test/resources/bigJsonTest.json";
        String stringUrl = "https://justjoin.it/api/offers";

        App app = new App();

        DatabaseService dbService = new DatabaseService();

        app.run(4, 1, 500, 3, 3, path, dbService);
    }
}
