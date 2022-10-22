package ru.sekret.tikhon.telegrambot.budgetbot.commands;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.sekret.tikhon.telegrambot.budgetbot.constants.Texts;

import java.io.Serializable;

public class HelpCommand extends AbstractCommand{

    public static BotApiMethod<? extends Serializable> helpMessage(Long chatId) {
        return sendMessage(chatId, Texts.HELP);
    }

    public static BotApiMethod<? extends Serializable> sendUnsupportedMessage(Long chatId) {
        final String text = Texts.standardHelp("Данный формат сообщения не поддерживается.");
        return sendMessage(chatId, text);
    }

}
