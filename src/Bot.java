import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.Math.toIntExact;

public class Bot extends TelegramLongPollingBot {
    //https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    //https://habrahabr.ru/post/136942/

    private static String welcome = " , добро пожаловать в Технопарк!\nМы будем присылать Вам сообщения о наших событиях.";
    private String token;
    private String botUsername;
    private String chat_id = "@technoparkevents";

    private enum Status {
        Yes("Yes"), Maybe("Maybe");

        Status(String data) {
            this.data = data;
        }

        String data;
    }

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
        if (update.hasCallbackQuery()) {
            Status st = Status.valueOf(update.getCallbackQuery().getData());
            String msg = update.getCallbackQuery().getMessage().getText();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            EditMessageText editedText = new EditMessageText();
            String[] lines = msg.split("\n");
            int yes = Integer.valueOf(lines[lines.length - 2].split(": ")[1]);
            int maybe = Integer.valueOf(lines[lines.length - 1].split(": ")[1]);
            switch (st) {
                case Yes:
                    msg = msg.replace("Пойдут: " + yes, "Пойдут: " + String.valueOf(yes + 1));
                    break;
                case Maybe:
                    msg = msg.replace("Может пойдут: " + maybe, "Может пойдут: " + String.valueOf(maybe + 1));
                    break;
            }
            editedText
                    .setChatId(chat_id).setMessageId(toIntExact(message_id))
                    .setText(msg).setReplyMarkup(generateMarkup());
            try {
                execute(editedText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMsg(String message, boolean isEvent) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        //Если isEvent, то надо добавить кнопки "пойду", "может пойду"
        if (isEvent) {
            sendMessage.setReplyMarkup(generateMarkup());
            message += "\nПойдут: 0\nМожет пойдут: 0";
        }
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup generateMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Пойду").setCallbackData(Status.Yes.data));
        rowInline.add(new InlineKeyboardButton().setText("Может пойду").setCallbackData(Status.Maybe.data));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
