package ru.sekret.tikhon.telegrambot.budgetbot.storage;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private static final Map<Long, String> currency = new HashMap<>();

    @NonNull
    public static String getUserCurrency(@NonNull Long chatId) { return currency.getOrDefault(chatId, "руб."); }
}
