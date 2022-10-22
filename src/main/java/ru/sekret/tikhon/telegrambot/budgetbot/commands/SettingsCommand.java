package ru.sekret.tikhon.telegrambot.budgetbot.commands;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.sekret.tikhon.telegrambot.budgetbot.constants.Texts;

import java.io.Serializable;

public class SettingsCommand extends AbstractCommand {
    public static final String PREFIX = "setting:";

    public static BotApiMethod<? extends Serializable> newMessage(@NonNull Long chatId, @NonNull String messageText) {//todo
        return sendMessage(chatId, Texts.NOT_IMPLEMENTED);//todo
    }

    public static BotApiMethod<? extends Serializable> updateMessage(@NonNull Long chatId, @NonNull Integer messageId, @NonNull String data) {
        return editMessage(chatId, messageId, Texts.NOT_IMPLEMENTED);
    }

}
