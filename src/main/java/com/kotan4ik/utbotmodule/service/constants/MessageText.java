package com.kotan4ik.utbotmodule.service.constants;

public class MessageText {
    public static final String GENERAL_WELCOME = "Привет! Я могу сохранить показания счетчиков и подсчитать начисленную по ним сумму. Нажми на кнопку, чтобы выбрать желаемое действие";
    public static final String SAVE_READINGS_WELCOME = "Пришли новые показания в формате Т1;T2;T3;Гор;Хол";
    public static final String SAVE_TARIFFS_WELCOME = "Пришли новые тарифы в формате Т1;T2;T3;Гор;Хол. Используй запятую для отделения копеек от рублей";
    public static final String TARIFFS_SAVED = "Тарифы добавлены";
    public static final String READINGS_SAVED = "Показания сохранены";
    public static final String CALCULATION_WAITING = "Сейчас посчитаю";
    public static final String CALCULATION_DONE = "Итого к оплате: ";
    public static final String TARIFFS_NOT_FOUND = "Нет сохраненных тарифов. Добавь их и попробуй еще раз";
    public static final String READINGS_NOT_FOUND = "Должны быть сохранены хотя бы 2 набора показаний. Добавь их и попробуй еще раз";
    public static final String DATA_PARSE_ERROR = "Не получилось обработать полученные данные. Убедись, что они в правильном формате и попробуй еще раз";
}
