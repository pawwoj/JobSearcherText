import application.App;

public class Main {
    public static void main(String[] args) {
        String path = "src/test/resources/bigJsonTest.json";
        String stringUrl = "https://justjoin.it/api/offers";

        App app = new App();

        app.run(2,100,5,3,4, stringUrl);
    }
}
