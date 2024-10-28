package com.kotan4ik.utbotmodule;

public enum ChatMode {
    /** Описывает возможные ветки диалога с ботом.
     *  При обновлении значений необходимо обновить тип MODE_TYPE, используемый в БД
     *  На основе этого класса генерируются кнопки для управления диалогом с ботом,
     *  а в БД сохраняются значения из этого класса, если для выполнении команды требуется получить новые сообщения от пользователя.
     *  Переменная buttonText хранит значение, которое будет выводится в кнопке управления диалогом.
     *  Переменная command хранит имя команды для этой кнопки.
     *  В ходе обработки полученных сообщений значения используются имена команд для вызова соответствующих функций бота
     */

    RECEIVE_NEW_READINGS("newReadings", "Передать новые показания в бот"),
    CALCULATE_SUM("calculateSum", "Расчет оплаты"),
    SEND_READINGS("sendReadings", "Передать показания в УК"),
    SAVE_TARIFFS("saveTariffs", "Сохранить новые тарифы на услуги");
    private final String command;
    private final String buttonText;

    ChatMode(String command, String buttonText) {
        this.command = command;
        this.buttonText = buttonText;
    }

    public String getCommand() {
        return command;
    }

    public String getButtonText() {
        return buttonText;
    }

    public static ChatMode getByCommand(String command) {
        for(ChatMode mode : ChatMode.values()) {
            if (mode.getCommand().equals(command))
                return mode;
        }
        throw new IllegalArgumentException("No mode found for this command");
    }
}
