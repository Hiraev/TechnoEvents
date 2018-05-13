import database.Database;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    //https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    //https://habrahabr.ru/post/136942/

    private Database db;
    private static String welcome = " , добро пожаловать в Технопарк!\nМы будем присылать Вам сообщения о наших событиях.";
    private String token;
    private String botUsername;

    public Bot() {
        try {
            db = new Database();
            db.connect();
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/telegram.cfg"));
            token = properties.getProperty("bot_token");
            botUsername = properties.getProperty("bot_username");
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We publish if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            long chat_id = msg.getChatId();
            int user_id = msg.getFrom().getId();
            String user_name = msg.getFrom().getUserName();
            String message_text = update.getMessage().getText();

            if (message_text.equals("/start")) {
                db.addUser(user_id, chat_id, user_name);
                String request = user_name + welcome;
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(request);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMsg(String message) {
        if (db.getAllChatIDs().isEmpty()) return;
        SendMessage sendMessage = new SendMessage()
                .setText(message);
        for (long id : db.getAllChatIDs()) {
            sendMessage.setChatId(id);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
