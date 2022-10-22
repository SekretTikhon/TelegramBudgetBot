package ru.sekret.tikhon.telegrambot.budgetbot;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sekret.tikhon.telegrambot.budgetbot.commands.HelpCommand;
import ru.sekret.tikhon.telegrambot.budgetbot.commands.RecordCommand;

import java.io.Serializable;
import java.util.Random;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod<? extends Serializable> method = update.hasMessage() ? onMessageReceived(update.getMessage()) :
                update.hasCallbackQuery() ? onCallbackQueryReceived(update.getCallbackQuery()) : null;

        if (method != null)
            execute(method);
    }

    public BotApiMethod<? extends Serializable> onMessageReceived(@NonNull Message message) {
        final Long chatId = message.getChatId();
        final String messageText = message.getText();
        if (messageText == null) {
            return HelpCommand.sendUnsupportedMessage(chatId);
        } else if (messageText.equals("/help")) {
            return HelpCommand.helpMessage(chatId);
        } else if (Pattern.matches("(\\d{1,18}\\.\\d{1,2})|(\\d{1,18})", messageText)) {
            return RecordCommand.newRecord(chatId, messageText);
        } else {
            return HelpCommand.sendUnsupportedMessage(chatId);
        }
    }

    public BotApiMethod<? extends Serializable> onCallbackQueryReceived(@NonNull CallbackQuery callbackQuery) {
        final Message message = callbackQuery.getMessage();
        final Long chatId = message.getChatId();
        final Integer messageId = message.getMessageId();
        final String data = callbackQuery.getData() == null ? "" : callbackQuery.getData();
        if (data.startsWith(RecordCommand.PREFIX)) {
            return RecordCommand.updateRecord(chatId, messageId, data.substring(RecordCommand.PREFIX.length()));
        }
        return null;
    }

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Must be only 2 arg: BotUsername and BotToken");
        String botUsername = args[0];
        String botToken = args[1];

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        try {
            api.registerBot(new Bot(botUsername, botToken));
            System.out.println("Started");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
