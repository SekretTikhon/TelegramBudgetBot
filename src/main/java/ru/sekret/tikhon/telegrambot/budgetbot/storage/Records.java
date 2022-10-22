package ru.sekret.tikhon.telegrambot.budgetbot.storage;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Records {
    private static final Map<Record.Type, Map<Long, Map<Integer, String>>> categories = new HashMap<>(2);// type 2 chatId 2 categoryId 2 categoryName
    static {
        categories.put(Record.Type.expenses, new HashMap<>());
        categories.put(Record.Type.income, new HashMap<>());
    }
    //private static final Map<Long, Map<Integer, String>> incomeCategories = new HashMap<>();// chatId 2 categoryId 2 categoryName
    private static final Map<Long, Map<Integer, Record>> unconfirmedRecords = new HashMap<>();// chatId 2 messageId 2 record
    private static final Map<Long, Map<Integer, Record>> confirmedRecords = new HashMap<>();// chatId 2 messageId 2 record

    @NonNull
    public static Map<Integer, String> getExpenseCategories(@NonNull Long chatId) {
        return getCategories(chatId, Record.Type.expenses);
    }

    @NonNull
    public static Map<Integer, String> getIncomeCategories(@NonNull Long chatId) {
        return getCategories(chatId, Record.Type.income);
    }

    @NonNull
    public static Map<Integer, String> getCategories(@NonNull Long chatId, @NonNull Record.Type type) {
        return categories.get(type).computeIfAbsent(chatId, ch -> new HashMap<>());
    }

    @NonNull
    public static Map<Integer, Record> getUnconfirmedRecords(@NonNull Long chatId) {
        return unconfirmedRecords.computeIfAbsent(chatId, ch -> new HashMap<>());
    }

    @NonNull
    public static Map<Integer, Record> getConfirmedRecords(@NonNull Long chatId) {
        return confirmedRecords.computeIfAbsent(chatId, ch -> new HashMap<>());
    }

    static {
        /*
        final Map<Integer, String> myExpensesCategories = getExpenseCategories(405873126L);
        myExpensesCategories.put(0, "Категория 0");
        myExpensesCategories.put(1, "Категория 1");
        myExpensesCategories.put(2, "Категория 2");
        myExpensesCategories.put(3, "Категория 3");

         */
    }

}
