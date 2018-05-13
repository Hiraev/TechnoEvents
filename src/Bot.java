import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    //https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    //https://habrahabr.ru/post/136942/

    private static String welcome = " , добро пожаловать в Технопарк!\nМы будем присылать Вам сообщения о наших событиях.";
    private String token;
    private String botUsername;

    public Bot() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/telegram.cfg"));
            token = properties.getProperty("bot_token");
            botUsername = properties.getProperty("bot_username");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //Здесь должна быть обработка нажатий на кнопки "пойду", "может пойду"
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMsg(String message, boolean isEvent) {
        //Если isEvent, то надо добавить кнопки "пойду", "может пойду"
        SendMessage sendMessage = new SendMessage()
                .setText(message);
        sendMessage.setChatId("@technoparkevents");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
