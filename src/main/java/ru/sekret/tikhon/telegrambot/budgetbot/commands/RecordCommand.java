package ru.sekret.tikhon.telegrambot.budgetbot.commands;

import com.google.common.collect.Lists;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sekret.tikhon.telegrambot.budgetbot.constants.Texts;
import ru.sekret.tikhon.telegrambot.budgetbot.storage.Record;
import ru.sekret.tikhon.telegrambot.budgetbot.storage.Records;
import ru.sekret.tikhon.telegrambot.budgetbot.storage.Settings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordCommand extends AbstractCommand {
    public static final String PREFIX = "rec:";
    private static class Action {
        public static final String NEW = "new";
        public static final String CATEGORY = "cat";
        public static final String EXPENSES = "exp";
        public static final String INCOME = "inc";
        public static final String DATE = "date";
        public static final String CANCEL = "cancel";
        public static final String APPROVE = "approve";
    }

    //private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    public static BotApiMethod<? extends Serializable> newRecord(@NonNull Long chatId, @NonNull String messageText) {//todo
        try {
            final BigDecimal amount = new BigDecimal(messageText);
            final String text = newRecordText(chatId, amount);
            final InlineKeyboardMarkup replyMarkup = newRecordKeyboard(amount);
            return sendMessage(chatId, text, replyMarkup);
        } catch (NumberFormatException e) {
            final String text = Texts.helpWithError("Не удалось создать новую запись.", String.format("Failed to create new record. Amount must be a BigDecimal. Amount: '%s'", messageText));
            return sendMessage(chatId, text);
        }

    }

    //rec:new:<amount>              - инициировать новую запись с данной суммой, дать выбрать категорию
    //rec:exp                       - установить тип записи на расход, удалить категорию и дать снова выбор
    //rec:inc                       - установить тип записи на доход,  удалить категорию и дать снова выбор
    //rec:cat                       - удалить категорию и дать снова выбор
    //rec:cat:категория 1           - установить эту категорию
    //rec:date-del                  - удалить дату и дать снова выбор
    //rec:date-custom:2022-09-10    - ручной выбор даты
    //rec:date-set:2022-09-10       - установить эту дату
    //rec:cancel                    - отменить эту запись, удалить ее черновик
    //rec:approve                   - подтвердить и записать эту запись
    //<T extends Serializable, Method extends BotApiMethod<T>>
    public static BotApiMethod<? extends Serializable> updateRecord(@NonNull Long chatId, @NonNull Integer messageId, @NonNull String data) {
        try {
            Record record = Records.getUnconfirmedRecords(chatId).get(messageId);

            // create or update record
            final String[] arr = data.split(":", 2);
            final String action = arr[0];
            if (!action.equals(Action.NEW) && record == null)
                throw new IllegalArgumentException(String.format("Failed to update record. Record with chatId:%d messageId:%d not found", chatId, messageId));
            switch (action) {
                case Action.NEW:
                    if (arr.length < 2)
                        throw new IllegalArgumentException(String.format("Failed to update record. Action '%s' must have parameter with ':' separator. Data: '%s'", action, data));
                    if (record != null)
                        throw new IllegalArgumentException(String.format("Failed to update record. Record with chatId:%d messageId:%d already exists.", chatId, messageId));

                    try {
                        final BigDecimal amount = new BigDecimal(arr[1]);
                        Records.getUnconfirmedRecords(chatId).put(messageId, record = new Record(amount));
                        break;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(String.format("Failed to update record. Amount must be a BigDecimal. Data: '%s'", data));
                    }
                case Action.CATEGORY:
                    final Integer catId = arr.length < 2 ? null : Integer.valueOf(arr[1]);
                    record.setCategoryId(catId);
                    break;
                case Action.EXPENSES:
                    record.setCategoryId(null);
                    record.setType(Record.Type.expenses);
                    break;
                case Action.INCOME:
                    record.setCategoryId(null);
                    record.setType(Record.Type.income);
                    break;
                case Action.DATE:
                    final String date = arr.length < 2 ? null : arr[1];
                    record.setDate(date);
                    break;
                case Action.APPROVE:
                    if (record.getCategoryId() != null && record.getDate() != null) {
                        Records.getConfirmedRecords(chatId).put(messageId, record);
                        Records.getUnconfirmedRecords(chatId).remove(messageId);
                        return null;//todo ответ что запись успешно добавлена
                    } else {
                        throw new IllegalArgumentException("Failed to approve record. Record have null fields.");
                    }
                case Action.CANCEL:
                    Records.getUnconfirmedRecords(chatId).remove(messageId);
                    return null;//todo ответ что запись удалена
                default:
                    throw new IllegalArgumentException(String.format("Failed to update record. Action '%s' not supported.", action));

            }

            // send answer
            //todo choice answer
            if (record.getCategoryId() == null) {
                // предложить выбрать категорию
                final Map<Integer, String> categories = Records.getCategories(chatId, record.getType());
                final String text = categories.isEmpty()
                        ? emptyCategoriesText(record.getType())
                        : choiceCategoryText(chatId, record.getAmount());
                final InlineKeyboardMarkup replyMarkup = categories.isEmpty()
                        ? emptyCategoriesKeyboard()
                        : choiceCategoryKeyboard(Records.getCategories(chatId, record.getType()));
                return editMessage(chatId, messageId, text, replyMarkup);
            } else if (record.getDate() == null) {
                //todo предложить выбрать дату

            } else {
                //todo предложить подтвердить запись
            }

            return editMessage(chatId, messageId, "Этот код еще не реализован");
        } catch (IllegalArgumentException ex) {
            return editMessage(chatId, messageId, Texts.helpWithError("Не удалось обновить запись.", ex.getMessage()));
        }
    }

    private static String newRecordText(Long chatId, BigDecimal amount) {
        return String.format("Обнаружена новая запись с суммой %s %s", amount, Settings.getUserCurrency(chatId));
    }

    private static InlineKeyboardMarkup newRecordKeyboard(BigDecimal amount) {
        final List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        final InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Продолжить")
                .callbackData(String.format("%s%s:%s", PREFIX, Action.NEW, amount))
                .build();
        lines.add(Lists.newArrayList(button));
        return new InlineKeyboardMarkup(lines);
    }

    private static String choiceCategoryText(Long chatId, BigDecimal amount) {
        return String.format("В какую категорию добавить %s %s?", amount, Settings.getUserCurrency(chatId));
    }

    private static InlineKeyboardMarkup choiceCategoryKeyboard(Map<Integer, String> categories) {
        final List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        for (Map.Entry<Integer, String> category : categories.entrySet()) {
            final InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(category.getValue())
                    .callbackData(String.format("%s%s:%d", PREFIX, Action.CATEGORY, category.getKey()))
                    .build();
            lines.add(Lists.newArrayList(button));
        }
        return new InlineKeyboardMarkup(lines);
    }

    private static String emptyCategoriesText(@NonNull Record.Type type) {
        return String.format("У Вас еще нет ни одной категории для %s. Добавьте категории и после этого попробуйте еще раз\n%s", type.equals(Record.Type.expenses) ? "расходов" : "доходов", Texts.FOR_SETTINGS);
    }

    private static InlineKeyboardMarkup emptyCategoriesKeyboard() {
        final List<List<InlineKeyboardButton>> lines = new ArrayList<>();
        final InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Попробовать еще раз")
                .callbackData(String.format("%s%s", PREFIX, Action.CATEGORY))
                .build();
        lines.add(Lists.newArrayList(button));
        return new InlineKeyboardMarkup(lines);
    }

}
