package ru.sekret.tikhon.telegrambot.budgetbot.constants;

import lombok.NonNull;

public class Texts {
    public static final String NOT_IMPLEMENTED = "This method not implemented yet";
    public static final String SOMETHING_WRONG = "Что-то пошло не так.";
    public static final String FOR_SETTINGS = "Для добавления или изменения категорий, а также других настроек бота нажмите /settings.";
    public static final String FOR_HELP = "Для помощи нажмите /help.";
    public static final String HELP =
            "Для настройки бота нажмите /settings\n" +
                    "Для добавления новой записи напишите сумму дохода или расхода, например:\n" +
                    "147\n" +
                    "148.2\n" +
                    "149.69\n" +
                    "и следуйте дальнейшим инструкциям.";

    public static String helpWithError(String error) {
        return String.format("%s\nError: %s", standardHelp(), error);
    }

    public static String helpWithError(String prefix, String error) {
        return String.format("%s\nError: %s", standardHelp(prefix), error);
    }

    public static String standardHelp() {
        return standardHelp(SOMETHING_WRONG);
    }

    public static String standardHelp(@NonNull String prefix) {
        return String.format("%s\n%s\n%s", prefix, FOR_SETTINGS, FOR_HELP);
    }
}
