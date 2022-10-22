package ru.sekret.tikhon.telegrambot.budgetbot.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class Record {
    @NonNull
    private final BigDecimal amount;
    @NonNull
    private Type type = Type.expenses;
    private Integer categoryId;
    private String date;

    public enum Type {
        expenses,
        income
    }

}
