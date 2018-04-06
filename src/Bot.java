import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    //https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    //https://habrahabr.ru/post/136942/

    private static List<Long> ids = Arrays.asList(48392275L, 381797073L);
    private static String welcome = " , добро пожаловать в Технопарк!\nМы будем присылать Вам сообщения о наших событиях.";

    @Override
    public String getBotToken() {
        return "/написать";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We publish if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String userName = update.getMessage().getFrom().getUserName();
            String request = userName + welcome;
            String message_text = update.getMessage().getText();
            if (message_text.equals("/start")) {
                long chat_id = update.getMessage().getChatId();
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
        return "/написать";
    }


    public void sendMsg(String message) {
        if (ids.isEmpty()) return;
        SendMessage sendMessage = new SendMessage()
                .setText(message);
        for (long id : ids) {
            sendMessage.setChatId(id);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
