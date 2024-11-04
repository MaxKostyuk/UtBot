package com.kotan4ik.utbotmodule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Buttons {
    /** Описывает возможные команды в диалоге с ботом.
     *  Переменная buttonText хранит значение, которое будет выводится в кнопке управления диалогом.
     *  Переменная command хранит имя команды для этой кнопки.
     *  В ходе обработки полученных сообщений значения используются имена команд для вызова соответствующих функций бота
     *  Также используется для хранения режима диалога с пользователем, когда требуется получить новые сообщения от пользователя.
     *  В этом случае значение записывается в БД.
     *  А при получении нового сообщения, не содержащего команду, проверяется наличие записи с режимом в БД для пользователя.
     */

    RECEIVE_NEW_READINGS(true, "newReadings", "Передать новые показания в бот"),
    CALCULATE_SUM(true, "calculateSum", "Расчет оплаты"),
    SAVE_TARIFFS(true, "saveTariffs", "Сохранить новые тарифы на услуги"),
    CANCEL(false, "cancel", "Отменить");
    private final boolean isMenuButton;
    private final String command;
    private final String buttonText;

    public static Buttons getByCommand(String command) {
        for(Buttons mode : Buttons.values()) {
            if (mode.getCommand().equals(command))
                return mode;
        }
        throw new IllegalArgumentException("No mode found for this command");
    }

    public static Stream<Buttons> stream() {
        return Stream.of(Buttons.values());
    }
}
