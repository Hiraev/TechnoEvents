import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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

        Publisher publisher = VkPublisher.getInstance();
        publisher.start(this::sendPost);
    }

    private void sendPost(Post post, boolean isEvent) {
        bot.sendMsg(post.toString(), isEvent);
    }
}
