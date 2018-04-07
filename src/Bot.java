import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    //https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/content/chapter1.html
    //https://habrahabr.ru/post/136942/

    private static String party_proper = new String(Character.toChars(0x1F389));
    private static String confetti_ball = new String(Character.toChars(0x1F38A));
    private static String graduation_cap = new String(Character.toChars(0x1F393));
    private static String speech_balloon = new String(Character.toChars(0x1F4AC));
    private static String mobile_phone = new String(Character.toChars(0x1F4F2));

    private static List<Long> ids = Arrays.asList();
    private static String answer = " , добро пожаловать в Технопарк! " + party_proper + confetti_ball
            + "\nМы будем осведомлять Вас " + speech_balloon +
            " о наших событиях" + mobile_phone + graduation_cap;

    @Override
    public String getBotToken() {
        return "/write";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We publish if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String userName = update.getMessage().getFrom().getUserName();
            int userId = update.getMessage().getFrom().getId();
            String request = userName + answer;
            String message_text = update.getMessage().getText();
            if (message_text.equals("/start")) {
                System.out.println("User id: " + userId);
                long chatId = update.getMessage().getChatId();
                System.out.println("Chat id: " + chatId);
                SendMessage answerMessage = new SendMessage()
                        .setChatId(chatId)
                        .setText(request);
                setInlineButton(answerMessage); // Test our two callBackButtons
                try {
                    execute(answerMessage); // Sending our answerMessage object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) { // Event test
            SendMessage answerMessage = new SendMessage();
            String data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (data.equals("0")) {
                String answerOnYes = "Спасибо, мы Вас ждем";
                answerMessage.setChatId(chatId).setText(answerOnYes);
            } else {
                String answerOnNo = "Тогда идите нахер";
                answerMessage.setChatId(chatId).setText(answerOnNo);
            }
            try {
                execute(answerMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "/write";
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

    // Installation of callbackButtons: will go and maybe
    private void setInlineButton(SendMessage message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().setText("Пойду").setCallbackData("0"));
        row.add(new InlineKeyboardButton().setText("Возможно пойду").setCallbackData("1"));
        rowsInline.add(row);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
    }


}
