package ru.sekret.tikhon.telegrambot.budgetbot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public abstract class AbstractCommand {
    @Deprecated
    protected static SendMessage sendErrorMessage(Long chatId, String errorText) throws IllegalArgumentException {
        if (chatId != 0) sendMessage(chatId, errorText);
        throw new IllegalArgumentException(errorText);
    }

    protected static SendMessage sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, null);
    }

    protected static SendMessage sendMessage(Long chatId, String text, ReplyKeyboard replyMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyMarkup);
        return sendMessage;
    }

    protected static EditMessageText editMessage(Long chatId, Integer messsageId, String text) {
        return editMessage(chatId, messsageId, text, null);
    }

    protected static EditMessageText editMessage(Long chatId, Integer messsageId, String text, InlineKeyboardMarkup replyMarkup) {
        final EditMessageText editMessage = new EditMessageText();
        editMessage.enableMarkdown(true);
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messsageId);
        editMessage.setText(text);
        editMessage.setReplyMarkup(replyMarkup);
        return editMessage;
    }


}
