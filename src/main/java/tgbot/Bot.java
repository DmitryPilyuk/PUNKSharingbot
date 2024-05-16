package tgbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgbot.model.Config;
import tgbot.model.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    State state = State.OTHER;
    Progress progress = Progress.CATEGORY;
    @Override
    public String getBotUsername() {
        return "PUNKSharingbot";
    }

    @Override
    public String getBotToken() {
        return Config.token;
    }

    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (state == State.CREATING){
                switch (progress) {
                    case START:
                        sendText(message, "Введите название категории");
                        progress = Progress.CATEGORY;
                        break;
                    case CATEGORY:
                        sendText(message, "Введите название предмета");
                        progress = Progress.NAME;
                        break;
                    case NAME:
                        sendText(message, "Напишите описание предмета");
                        progress = Progress.DESCRIPTION;
                        break;
                    case DESCRIPTION:
                        sendText(message, "Введите номер общежития");
                        progress = Progress.DORMITORY;
                        break;
                    case DORMITORY:
                        sendText(message, "Введите стоимость аренды");
                        progress = Progress.PRICE;
                        break;
                    case PRICE:
                        sendText(message, "Пришлите фото");
                        progress = Progress.PHOTO;
                        break;
                    case PHOTO:
                        sendText(message, "Вот анкета вашего предмета:");
                        sendItem1(message, true);
                        progress = Progress.START;
                        state = State.OTHER;
                        break;
                }
            }
            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("/start")) {
                    sendMainMenuButtons(message);
                } else if (text.equals("Арендатор")) {
                    sendRenterMenuButtons(message);
                } else if (text.equals("Клиент")) {
                    sendClientMenuButtons(message);

                }
            }
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("add")) {
                state = State.CREATING;
                sendText(message, "Введите название категории");
            } else if (data.equals("show")) {
                sendItem1(message, true);
                sendItem2(message, true);
            } else if (data.equals("sport")) {
                sendCategoryMenuButtons(message);
            } else if (data.equals("bike")) {
                sendItem2(message, false);
            }

        }

    }

    private void sendText(Message message, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(message.getChatId().toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
    private void sendMainMenuButtons(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Здравствуйте, выберите режим использования.");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId().toString());

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        keyboardButton1.setText("Арендатор");
        KeyboardButton keyboardButton2 = new KeyboardButton();
        keyboardButton2.setText("Клиент");
        keyboardRow1.add(keyboardButton1);
        keyboardRow2.add(keyboardButton2);
        keyboardRowList.add(keyboardRow1);
        keyboardRowList.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {execute(sendMessage);} catch (TelegramApiException e) {e.printStackTrace();}
    }

    private void sendRenterMenuButtons(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите действие:");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId().toString());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Добавить");
        inlineKeyboardButton1.setCallbackData("add");
        inlineKeyboardButton2.setText("Показать список");
        inlineKeyboardButton2.setCallbackData("show");
        inlineKeyboardButtonList.add(inlineKeyboardButton1);
        inlineKeyboardButtonList.add(inlineKeyboardButton2);
        inlineButtons.add(inlineKeyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {execute(sendMessage);} catch (TelegramApiException e) {e.printStackTrace();}
    }

    private void sendClientMenuButtons(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выбери категорию:");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId().toString());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList1 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList2 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList3 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList4 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Спорт");
        inlineKeyboardButton1.setCallbackData("sport");
        inlineKeyboardButton2.setText("Бытовая техника");
        inlineKeyboardButton2.setCallbackData("byt");
        inlineKeyboardButton3.setText("Кухня");
        inlineKeyboardButton3.setCallbackData("kitchen");
        inlineKeyboardButton4.setText("Остальное");
        inlineKeyboardButton4.setCallbackData("othercat");
        inlineKeyboardButtonList1.add(inlineKeyboardButton1);
        inlineKeyboardButtonList2.add(inlineKeyboardButton2);
        inlineKeyboardButtonList3.add(inlineKeyboardButton3);
        inlineKeyboardButtonList4.add(inlineKeyboardButton4);
        inlineButtons.add(inlineKeyboardButtonList1);
        inlineButtons.add(inlineKeyboardButtonList2);
        inlineButtons.add(inlineKeyboardButtonList3);
        inlineButtons.add(inlineKeyboardButtonList4);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {execute(sendMessage);} catch (TelegramApiException e) {e.printStackTrace();}
    }
    private void sendCategoryMenuButtons(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Предметы категории спорт:");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId().toString());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList1 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList2 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Мяч");
        inlineKeyboardButton1.setCallbackData("ball");
        inlineKeyboardButton2.setText("Велосипед");
        inlineKeyboardButton2.setCallbackData("bike");
        inlineKeyboardButton3.setText("Остальное");
        inlineKeyboardButton3.setCallbackData("otheritems");
        inlineKeyboardButtonList1.add(inlineKeyboardButton1);
        inlineKeyboardButtonList2.add(inlineKeyboardButton2);
        inlineKeyboardButtonList3.add(inlineKeyboardButton3);
        inlineButtons.add(inlineKeyboardButtonList1);
        inlineButtons.add(inlineKeyboardButtonList2);
        inlineButtons.add(inlineKeyboardButtonList3);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {execute(sendMessage);} catch (TelegramApiException e) {e.printStackTrace();}
    }

    private void sendItem1(Message message, boolean withButton){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption("Название: Пылесос\nКатегория: Бытовая техника\nОписание: Беспроводной мощный пылесос\nЦена: 50 руб/час\nОбщежитие №12\ntg: @DmtrPlk");
        sendPhoto.setChatId(message.getChatId().toString());
        if (withButton) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Удалить");
            inlineKeyboardButton.setCallbackData("delete");
            inlineKeyboardButtonList.add(inlineKeyboardButton);
            inlineButtons.add(inlineKeyboardButtonList);
            inlineKeyboardMarkup.setKeyboard(inlineButtons);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }
        sendPhoto.setPhoto(new InputFile(new File("/home/dmitry/Programming/PUNKSharingbot/src/main/resources/img.png")));
        try {execute(sendPhoto);} catch (TelegramApiException e) {e.printStackTrace();}
    }
    private void sendItem(Message message, Item item, boolean withButton){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(String.format("Название: %s\nКатегория: %s\nОписание: %s\nЦена: %s\nОбщежитие №%d\ntg: %s", item.getName(), item.getCategory(), item.getDescription(), item.getPrice(), item.getDormitory(), item.getTg()));
        sendPhoto.setChatId(message.getChatId().toString());
        if (withButton) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Удалить");
            inlineKeyboardButton.setCallbackData("delete");
            inlineKeyboardButtonList.add(inlineKeyboardButton);
            inlineButtons.add(inlineKeyboardButtonList);
            inlineKeyboardMarkup.setKeyboard(inlineButtons);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }
        sendPhoto.setPhoto(new InputFile(new File("/home/dmitry/Programming/PUNKSharingbot/src/main/resources/img_1.png")));
        try {execute(sendPhoto);} catch (TelegramApiException e) {e.printStackTrace();}
    }
    private void sendItem2(Message message, boolean withButton){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption("Название: Велосипед\nКатегория: Спорт\nОписание: Велосипед горный, диаметр колеса 26 дюймов\nЦена: 500 руб/час\nОбщежитие №12\ntg: @DmtrPlk");
        sendPhoto.setChatId(message.getChatId().toString());
        if (withButton) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Удалить");
            inlineKeyboardButton.setCallbackData("delete");
            inlineKeyboardButtonList.add(inlineKeyboardButton);
            inlineButtons.add(inlineKeyboardButtonList);
            inlineKeyboardMarkup.setKeyboard(inlineButtons);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }
        sendPhoto.setPhoto(new InputFile(new File("/home/dmitry/Programming/PUNKSharingbot/src/main/resources/img_1.png")));
        try {execute(sendPhoto);} catch (TelegramApiException e) {e.printStackTrace();}
    }
}
