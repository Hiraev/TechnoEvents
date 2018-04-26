import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

//import database.Database;
//import java.sql.ResultSet;

public class Main {
    private Bot bot;

    public static void main(String[] args) {
        new Main().start();
    }

    private void start() {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(bot = new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        Publisher publisher = TestPublisher.getInstance();
        publisher.start(this::sendPost);
    }

    private void sendPost(String post) {
        bot.sendMsg(post);
    }
}
